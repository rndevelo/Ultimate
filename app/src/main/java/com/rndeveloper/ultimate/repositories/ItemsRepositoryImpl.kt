package com.rndeveloper.ultimate.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.notifications.NotificationAPI
import com.rndeveloper.ultimate.notifications.PushNotification
import com.rndeveloper.ultimate.utils.Constants.SPOT_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Utils.currentTime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ItemsRepositoryImpl @Inject constructor(
    private val fireAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val notificationAPI: NotificationAPI,
) : ItemsRepository {

    override fun getItems(collectionRef: String, directions: Directions): Flow<Result<List<Spot>>> =
        callbackFlow {

            val collection = fireStore.collection(collectionRef).document(directions.country)
                .collection(directions.area)
            val cutOff = currentTime() - TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES)

            collection
                .whereLessThan("timestamp", cutOff)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        snapshot.documents.forEach { document ->
//                            document.reference.delete().addOnSuccessListener {}
                        }
                    } else {
                        if (e != null) {
                            return@addSnapshotListener
                        }
                    }
                    collection
                        .addSnapshotListener { snapshotItems, error ->
                            val items = snapshotItems?.toObjects(Spot::class.java)
                            if (items != null) {
                                trySend(Result.success(items))
                            } else {
                                if (error != null) {
                                    trySend(Result.failure(error.fillInStackTrace()))
                                }
                            }
                        }
                }
            awaitClose()
        }

    override fun setSpot(pair: Pair<String, Spot>): Flow<Result<Boolean>> = callbackFlow {

        val tag = fireStore.collection(pair.first).document().id

        fireStore.collection(pair.first)
            .document(pair.second.directions.country)
            .collection(pair.second.directions.area)
            .document(tag)
            .set(pair.second.copy(tag = tag))
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
                        userRepository.setPoints(parameters.second.first, 5).collect {
                            notificationAPI.postNotification(PushNotification(parameters.second.second))
                        }
                    }

                    if (myUid != null) {
                        launch {
                            userRepository.setPoints(myUid, 2).collectLatest {}
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
    fun getItems(collectionRef: String, directions: Directions): Flow<Result<List<Spot>>>
    fun setSpot(pair: Pair<String, Spot>): Flow<Result<Boolean>>
    fun removeSpot(parameters: Pair<Triple<String, String, String>, Pair<String, String>>): Flow<Result<Boolean>>
}