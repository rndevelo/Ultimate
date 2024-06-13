package com.rndeveloper.ultimate.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.services.MyFirebaseMessagingService
import com.rndeveloper.ultimate.utils.Constants.USER_REFERENCE
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) : UserRepository {

    override fun getUserData(userUid: String): Flow<Result<User>> = callbackFlow {

        if (userUid.isEmpty()) {

            firebaseAuth.currentUser?.apply {

                val userAuthData = User().copy(
                    username = displayName ?: email!!,
                    email = email!!,
                    uid = uid,
                    photo = photoUrl.toString(),
                )

                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    MyFirebaseMessagingService.token = token

                    fireStore.collection(USER_REFERENCE).document(uid)
                        .addSnapshotListener { snapshot, e ->
                            val user = snapshot?.toObject(User::class.java)
                            if (user != null) {
                                userAuthData.copy(
                                    points = user.points,
                                    car = user.car,
                                    token = token
                                ).let { userData ->
                                    trySend(Result.success(userData))
                                }

                            } else {
                                userAuthData.copy(
                                    token = token
                                ).let { userData ->
                                    trySend(Result.success(userData))
                                }
//                            if (e != null) {
//                                trySend(Result.failure(e.fillInStackTrace()))
//                            }
                            }
                        }


                }.addOnFailureListener {
                    trySend(Result.failure(it.fillInStackTrace()))
                }

            }
        } else {
            fireStore.collection(USER_REFERENCE).document(userUid)
                .addSnapshotListener { snapshot, e ->
                    val user = snapshot?.toObject(User::class.java)
                    if (user != null) {
                        trySend(Result.success(user))

                    } else {
                        if (e != null) {
                            trySend(Result.failure(e.fillInStackTrace()))
                        }
                    }
                }
        }
        awaitClose()
    }

    override fun getHistoryData(): Flow<Result<List<Item>>> = callbackFlow {

        firebaseAuth.currentUser?.let {
            fireStore
                .collection(USER_REFERENCE)
                .document(it.uid)
                .collection("history").addSnapshotListener { snapshot, e ->
                    val items = snapshot?.toObjects(Item::class.java)
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

    override fun setUserData(user: User, uid: String): Flow<Result<Boolean?>> = callbackFlow {

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

    override fun deleteUserData(): Flow<Result<Boolean?>> = callbackFlow {
        firebaseAuth.currentUser?.apply {
            fireStore.collection(USER_REFERENCE).document(uid)
                .delete().addOnSuccessListener {
                    trySend(Result.success(true))
                }.addOnFailureListener {
                    trySend(Result.failure(it))
                }
        }
        awaitClose()
    }
}


interface UserRepository {
    fun getUserData(userUid: String): Flow<Result<User>>
    fun getHistoryData(): Flow<Result<List<Item>>>
    fun setUserData(user: User, uid: String): Flow<Result<Boolean?>>
    fun deleteUserData(): Flow<Result<Boolean?>>
}
