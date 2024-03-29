package com.rndeveloper.ultimate.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val userRepository: UserRepository,
) : LoginRepository {

    override fun loginEmailPass(email: String, password: String): Flow<Result<AuthResult>> =
        channelFlow {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    launch {
                        send(Result.success(it))
                    }
                }
                .addOnFailureListener {
                    launch {
                        send(Result.failure(it))
                    }
                }
            awaitClose()
        }


    //    INTENTAR USAR REGISTER Y LOGIN CON UNA SOLA FUNCION
    override fun register(email: String, password: String): Flow<Result<AuthResult>> =
        channelFlow {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    launch {
                        send(Result.success(it))
                    }
                }
                .addOnFailureListener {
                    launch {
                        send(Result.failure(it))
                    }
                }
            awaitClose()
        }

    override fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>> =
        channelFlow {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    launch {
                        send(Result.success(it))
                    }
                }
                .addOnFailureListener {
                    launch {
                        send(Result.failure(it))
                    }
                }
            awaitClose()
        }

    override fun getUserAuthentication(): Flow<Result<Boolean>> = channelFlow {
        firebaseAuth.addAuthStateListener {
            if (it.currentUser != null) {
                launch {
                    send(Result.success(true))
                }
            } else {
                launch {
                    send(Result.success(false))
                }
            }
        }
        awaitClose()
    }

    override fun recoverPassword(email: String): Flow<Result<Boolean>> = channelFlow {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                launch {
                    send(Result.success(it.isSuccessful))
                }
            }.addOnFailureListener {
                launch {
                    send(Result.failure(it))
                }
            }
        awaitClose()
    }

    override fun logout() {
        googleSignInClient.signOut().addOnSuccessListener {
            firebaseAuth.signOut()
        }
    }

    override fun deleteUser(): Flow<Result<Boolean>> = channelFlow {

        userRepository.deleteUserData().collectLatest {
            if (it.isSuccess) {
                firebaseAuth.currentUser?.delete()?.addOnSuccessListener {
                    launch {
                        send(Result.success(true))
                    }
                }?.addOnFailureListener {
                    launch {
                        send(Result.failure(it))
                    }
                }
            } else {
                it.exceptionOrNull()?.let { exception ->
                    launch {
                        send(Result.failure(exception))
                    }
                }
            }
        }
        awaitClose()
    }
}

interface LoginRepository {
    fun loginEmailPass(email: String, password: String): Flow<Result<AuthResult>>
    fun register(email: String, password: String): Flow<Result<AuthResult>>
    fun loginWithGoogle(idToken: String): Flow<Result<AuthResult>>
    fun getUserAuthentication(): Flow<Result<Boolean>>
    fun recoverPassword(email: String): Flow<Result<Boolean>>
    fun logout()
    fun deleteUser(): Flow<Result<Boolean>>
}
