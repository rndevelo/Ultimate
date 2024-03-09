package com.rndeveloper.ultimate.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.services.MyFirebaseMessagingService
import com.rndeveloper.ultimate.utils.Constants.USER_REFERENCE
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : UserRepository {

    override fun getUserData(): Flow<Result<User>> = callbackFlow {

        firebaseAuth.currentUser?.apply {
            val userAuthData = User().copy(
                username = displayName ?: "User",
                email = email ?: "user@gmail.com",
                uid = uid,
                photo = photoUrl.toString(),
            )

            fireStore.collection(USER_REFERENCE).document(uid)
                .addSnapshotListener { snapshot, e ->
                    val user = snapshot?.toObject(User::class.java)
                    if (user != null) {

                        if (user.token.isEmpty()) {

                            FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->
                                MyFirebaseMessagingService.token = task
                                launch {
                                    setUserData(
                                        userAuthData.copy(
                                            points = user.points,
                                            car = user.car,
                                            token = task
                                        )
                                    ).collectLatest { }
                                }
                            }.addOnFailureListener {
                                Log.d("GetToken", "${it.message}")
                            }
                        }

                        trySend(
                            Result.success(
                                userAuthData.copy(
                                    points = user.points,
                                    car = user.car,
                                    token = user.token
                                )
                            )
                        )
                    } else {
                        trySend(Result.success(userAuthData))
                        if (e != null) {
                            trySend(Result.failure(e.fillInStackTrace()))
                        }
                    }
                }
        }
        awaitClose()
    }

    override fun getHistoryData(): Flow<Result<List<Spot>>> = callbackFlow {

//        FIXME: getAddressList? and getSpots?
        firebaseAuth.currentUser?.let {
            fireStore
                .collection(USER_REFERENCE)
                .document(it.uid)
                .collection("history").addSnapshotListener { snapshot, e ->
                    val items = snapshot?.toObjects(Spot::class.java)
                    if (items != null) {
                        trySend(Result.success(items))
                    } else {
                        if (e != null) {
                            trySend(Result.failure(e.fillInStackTrace()))
                        }
                    }
                }
        }
        awaitClose()
    }

    override fun setUserData(user: User): Flow<Result<Boolean?>> = callbackFlow {

        firebaseAuth.currentUser?.apply {
            fireStore.collection(USER_REFERENCE).document(uid)
                .set(user).addOnSuccessListener {
                    trySend(Result.success(true))
                }.addOnFailureListener {
                    trySend(Result.failure(it))
                }
        }
        awaitClose()
    }

    override fun setPoints(
        uid: String,
        incrementPoints: Long
    ): Flow<Result<Boolean>> = callbackFlow {
        fireStore.collection(USER_REFERENCE).document(uid)
            .update("points", FieldValue.increment(incrementPoints)).addOnSuccessListener { _ ->
                trySend(Result.success(true))
            }
            .addOnFailureListener { error ->
                trySend(Result.failure(error))
            }
        awaitClose()
    }
}


interface UserRepository {
    fun getUserData(): Flow<Result<User>>
    fun getHistoryData(): Flow<Result<List<Spot>>>
    fun setUserData(user: User): Flow<Result<Boolean?>>

    fun setPoints(
        uid: String,
        incrementPoints: Long
    ): Flow<Result<Boolean>>
}
