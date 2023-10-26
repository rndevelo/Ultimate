package com.rndeveloper.ultimate.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : UserRepository {

    override fun getUserData(): Flow<Result<User?>> = callbackFlow {

        firebaseAuth.currentUser?.apply {
            fireStore.collection("USERS").document(uid)
                .addSnapshotListener { snapshot, e ->
                    val user = snapshot?.toObject(User::class.java)
                    if (user != null) {
                        trySend(
                            Result.success(
                                user.copy(
                                    username = displayName ?: "User",
                                    email = email ?: "user@gmail.com",
                                    uid = uid,
                                    photo = photoUrl.toString(),
                                )
                            )
                        )
                    } else {
                        if (e != null) {
                            trySend(Result.failure(e.fillInStackTrace()))
                        }
                    }
                }
        }
        awaitClose()
    }

    override fun setUserCar(car: Car): Flow<Result<Void?>> = callbackFlow {

        firebaseAuth.currentUser?.apply {
            fireStore.collection("USERS").document(uid)
                .update("car", car).addOnSuccessListener {
                    trySend(Result.success(it))

                }.addOnFailureListener {
                    trySend(Result.failure(it))
                }
        }
        awaitClose()
    }
}


interface UserRepository {
    fun getUserData(): Flow<Result<User?>>
    fun setUserCar(car: Car): Flow<Result<Void?>>
}
