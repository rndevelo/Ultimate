package com.rndeveloper.ultimate.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.backend.notifications.NotificationAPI
import com.rndeveloper.ultimate.backend.notifications.PushNotification
import com.rndeveloper.ultimate.utils.Constants.SPOT_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Constants.TIMESTAMP
import com.rndeveloper.ultimate.utils.Utils.currentTime
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ItemsRepositoryImpl @Inject constructor(
    private val fireAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val notificationAPI: NotificationAPI,
) : ItemsRepository {

    override fun getItems(collectionRef: String, directions: Directions): Flow<Result<List<Item>>> =
        callbackFlow {

            val collection = fireStore.collection(collectionRef).document(directions.country)
                .collection(directions.area)
            val cutOff = currentTime() - TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS)

            collection
                .whereLessThan(TIMESTAMP, cutOff)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        snapshot.documents.forEach { document ->
                            document.reference.delete().addOnSuccessListener {
                                getItemsBeforeDelete(collection)
                            }.addOnFailureListener {
                                getItemsBeforeDelete(collection)
                            }
                        }
                    } else {
                        getItemsBeforeDelete(collection)
                        if (e != null) {
                            return@addSnapshotListener
                        }
                    }

                }
            awaitClose()
        }

    private fun ProducerScope<Result<List<Item>>>.getItemsBeforeDelete(
        collection: CollectionReference
    ) {
        collection
            .addSnapshotListener { snapshotItems, error ->
                val items = snapshotItems?.toObjects(Item::class.java)
                if (items != null) {
                    trySend(Result.success(items))
                } else {
                    if (error != null) {
                        trySend(Result.failure(error.fillInStackTrace()))
                    }
                }
            }
    }

    override fun setSpot(parameters: Pair<String, Item>): Flow<Result<Boolean>> = callbackFlow {

        val tag = fireStore.collection(parameters.first).document().id

        fireStore.collection(parameters.first)
            .document(parameters.second.directions.country)
            .collection(parameters.second.directions.area)
            .document(tag)
            .set(parameters.second.copy(tag = tag))
            .addOnSuccessListener { _ ->
                trySend(Result.success(true))
            }
            .addOnFailureListener { error ->
                trySend(Result.failure(error))
            }
        awaitClose()
    }

    override fun removeSpot(parameters: Pair<Triple<String, String, String>, Pair<String, String>>): Flow<Result<Boolean>> =
        callbackFlow {

            val myUid = fireAuth.currentUser?.uid

            fireStore.collection(SPOT_COLLECTION_REFERENCE)
                .document(parameters.first.first)
                .collection(parameters.first.second)
                .document(parameters.first.third)
                .delete()
                .addOnSuccessListener { _ ->
                    launch {
                        val user = async {
                            userRepository.getUserData(parameters.second.first).firstOrNull()
                                ?.getOrNull()
                        }.await()
                        user?.copy(points = user.points + 5)?.let { userData->
                            userRepository.setUserData(userData).collectLatest {
                                notificationAPI.postNotification(PushNotification(parameters.second.second))
                            }
                        }
                    }

                    if (myUid != null) {
                        launch {
                            val user = async {
                                userRepository.getUserData(myUid).firstOrNull()
                                    ?.getOrNull()
                            }.await()
                            user?.copy(points = user.points + 2)?.let { userData->
                                userRepository.setUserData(userData).collectLatest {}
                            }
                        }
                    }
                }
                .addOnFailureListener { error ->
                    trySend(Result.failure(error))
                }

            awaitClose()
        }
}

interface ItemsRepository {
    fun getItems(collectionRef: String, directions: Directions): Flow<Result<List<Item>>>
    fun setSpot(parameters: Pair<String, Item>): Flow<Result<Boolean>>
    fun removeSpot(parameters: Pair<Triple<String, String, String>, Pair<String, String>>): Flow<Result<Boolean>>
}