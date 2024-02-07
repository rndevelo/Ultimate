package com.rndeveloper.ultimate.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.utils.Constants.ITEM_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Utils.currentTime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SpotRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : SpotRepository {

    override fun getSpots(collectionRef: String, directions: Directions): Flow<Result<List<Spot>>> =
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

    override fun removeSpot(spot: Spot): Flow<Result<Boolean>> = callbackFlow {

        val randomPoints = (2..5).random().toLong()

        fireStore.collection(ITEM_COLLECTION_REFERENCE)
            .document(spot.directions.country)
            .collection(spot.directions.area)
            .document(spot.tag)
            .delete()
            .addOnSuccessListener { _ ->
                fireStore
                    .collection("USERS")
                    .document(spot.user.uid)
                    .collection("history")
                    .document().set(spot)
                    .addOnSuccessListener {
                        if (spot.user.points <= 60) {
                            fireStore
                                .collection("USERS")
                                .document(spot.user.uid)
                                .update("points", FieldValue.increment(randomPoints))
                                .addOnSuccessListener {
                                    trySend(Result.success(true))
                                }.addOnFailureListener { error ->
                                    trySend(Result.failure(error))
                                }
                        } else {
                            trySend(Result.success(true))
                        }
                    }.addOnFailureListener { error ->
                        trySend(Result.failure(error))
                    }
            }
            .addOnFailureListener { error ->
                trySend(Result.failure(error))
            }

        awaitClose()
    }
}


interface SpotRepository {
    fun getSpots(collectionRef: String, directions: Directions): Flow<Result<List<Spot>>>
    fun setSpot(pair: Pair<String, Spot>): Flow<Result<Boolean>>
    fun removeSpot(spot: Spot): Flow<Result<Boolean>>
}