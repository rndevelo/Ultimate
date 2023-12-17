package com.rndeveloper.ultimate.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Spot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SpotRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
) : SpotRepository {

    override fun getSpots(directions: Directions): Flow<Result<List<Spot>>> = callbackFlow {

//        FIXME: getAddressList? and getSpots?
        fireStore
            .collection("ITEMS")
            .document(directions.country)
            .collection(directions.area).addSnapshotListener { snapshot, e ->
                val items = snapshot?.toObjects(Spot::class.java)
                if (items != null) {
                    trySend(Result.success(items))
                } else {
                    if (e != null) {
                        trySend(Result.failure(e.fillInStackTrace()))
                    }
                }
            }
        awaitClose()
    }

    override fun setSpot(spot: Spot): Flow<Result<Boolean>> = callbackFlow {

        val tag = fireStore.collection("ITEMS").document().id

        fireStore.collection("ITEMS")
            .document(spot.directions.country)
            .collection(spot.directions.area)
            .document(tag)
            .set(spot.copy(tag = tag))
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

        fireStore.collection("ITEMS")
            .document(spot.directions.country)
            .collection(spot.directions.area)
            .document(spot.tag)
            .delete()
            .addOnSuccessListener { _ ->
                fireStore.collection("USERS").document(spot.user.uid).collection("history")
                    .document().set(spot)
                    .addOnSuccessListener {
                        if (spot.user.points <= 60) {
                            fireStore.collection("USERS").document(spot.user.uid)
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
    fun getSpots(directions: Directions): Flow<Result<List<Spot>>>
    fun setSpot(spot: Spot): Flow<Result<Boolean>>
    fun removeSpot(spot: Spot): Flow<Result<Boolean>>
}