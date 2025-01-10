package com.guilherme.braintappers.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.guilherme.braintappers.R
import com.guilherme.braintappers.domain.FirebaseAccountDeletion
import com.guilherme.braintappers.domain.FirebaseCurrentUser
import com.guilherme.braintappers.domain.FirebaseEmailAndPasswordAuthError
import com.guilherme.braintappers.domain.FirebaseGoogleAuthError
import com.guilherme.braintappers.domain.FirebaseReauthenticate
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.FirebaseSignInWithEmailAndPasswordError
import com.guilherme.braintappers.domain.GetCredential
import com.guilherme.braintappers.domain.LinkAccountWithEmailError
import com.guilherme.braintappers.domain.LinkAccountWithGoogleError
import com.guilherme.braintappers.domain.ResetPasswordError
import com.guilherme.braintappers.domain.Result
import kotlinx.coroutines.tasks.await

class FirebaseImpl(private val context: Context) : FirebaseRepository {

    override suspend fun currentUser(): FirebaseUser? {
        return try {
            Firebase.auth.currentUser
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun createAnonymousAccount() {
        if (currentUser() == null) {
            Firebase.auth.signInAnonymously().await()
            println("Anonymous user created")
        } else {
            println("Anonymous user already created")
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<Unit, FirebaseEmailAndPasswordAuthError> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_AUTH_USER_COLLISION)
        } catch (e: FirebaseNetworkException) {
            Result.Error(FirebaseEmailAndPasswordAuthError.FIREBASE_NETWORK)
        } catch (e: Exception) {
            println(e)
            Result.Error(FirebaseEmailAndPasswordAuthError.UNKNOWN)
        }

    }

    override suspend fun signUpWithGoogle(idToken: String): Result<Unit, FirebaseGoogleAuthError> {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        return try {

            Firebase.auth.signInWithCredential(firebaseCredential).await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidUserException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseAuthUserCollisionException) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.FIREBASE_AUTH_USER_COLLISION)

        } catch (e: Exception) {

            Log.e(TAG, e.message.toString())
            Result.Error(FirebaseGoogleAuthError.UNKNOWN)

        }

    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<Unit, FirebaseSignInWithEmailAndPasswordError> {
        return try {

            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseSignInWithEmailAndPasswordError.UNKNOWN)

        }

    }

    override suspend fun linkAccountWithEmail(
        email: String,
        password: String
    ): Result<Unit, LinkAccountWithEmailError> {

        val credential = EmailAuthProvider.getCredential(email, password)

        return try {

            Firebase.auth.currentUser?.linkWithCredential(credential)?.await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthWeakPasswordException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH_WEAK_PASSWORD)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseAuthUserCollisionException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH_USER_COLLISION)

        } catch (e: FirebaseAuthInvalidUserException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_AUTH)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(LinkAccountWithEmailError.UNKNOWN)

        }
    }

    override suspend fun linkAccountWithGoogle(): Result<Unit, LinkAccountWithGoogleError> {

        when (val result = authenticateWithGoogle()) {
            is Result.Success -> {

                return try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(result.data.data)

                    val credential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    Firebase.auth.currentUser?.linkWithCredential(credential)?.await()

                    Result.Success(Unit)
                } catch (e: FirebaseAuthWeakPasswordException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH_WEAK_PASSWORD)

                } catch (e: FirebaseAuthInvalidCredentialsException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH_INVALID_CREDENTIALS)

                } catch (e: FirebaseAuthUserCollisionException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH_USER_COLLISION)

                } catch (e: FirebaseAuthInvalidUserException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH_INVALID_USER)

                } catch (e: FirebaseAuthException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_AUTH)

                } catch (e: FirebaseNetworkException) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.FIREBASE_NETWORK)

                } catch (e: Exception) {

                    e.printStackTrace()
                    Result.Error(LinkAccountWithGoogleError.UNKNOWN)

                }
            }

            is Result.Error -> {
                return when(result.error) {
                    GetCredential.GET_CREDENTIAL -> {
                        Result.Error(LinkAccountWithGoogleError.GET_CREDENTIAL)
                    }

                    GetCredential.UNKNOWN -> {
                        Result.Error(LinkAccountWithGoogleError.UNKNOWN)

                    }
                }
            }
        }

    }

    override suspend fun signOut() {
        try {

            val user = Firebase.auth.currentUser

            if (user?.isAnonymous == true) {
                user.delete().await()
            }

            Firebase.auth.signOut()

        } catch (e: Exception) {
            e.printStackTrace()
            println("Sign Out: $e")
        }
    }

    override suspend fun deleteAccount(): Result<Unit, FirebaseAccountDeletion> {
        return try {
            Firebase.auth.currentUser?.delete()?.await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthRecentLoginRequiredException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_AUTH_RECENT_LOGIN_REQUIRED)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseAccountDeletion.UNKNOWN)

        }
    }

    override suspend fun getCurrentUserProviderId(): Result<FirebaseProviderId, FirebaseCurrentUser> {

        val user = Firebase.auth.currentUser
        return if (user != null) {

            var providerId: Result<FirebaseProviderId, FirebaseCurrentUser> =
                Result.Error(FirebaseCurrentUser.UNEXPECTED_PROVIDER)

            for (profile in user.providerData) {
                // Id of the provider (ex: google.com)
                providerId = when (profile.providerId) {
                    "password" -> Result.Success(FirebaseProviderId.PASSWORD)
                    "google.com" -> Result.Success(FirebaseProviderId.GOOGLE)
                    else -> Result.Error(FirebaseCurrentUser.UNEXPECTED_PROVIDER)
                }

            }

            providerId

        } else {
            Result.Error(FirebaseCurrentUser.NULL_VALUE)
        }

    }

    override suspend fun reauthenticateWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit, FirebaseReauthenticate> {
        return try {

            val credential = EmailAuthProvider.getCredential(email, password)
            Firebase.auth.currentUser?.reauthenticate(credential)?.await()
            deleteAccount()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidUserException) {

            e.printStackTrace()
            Result.Error(FirebaseReauthenticate.FIREBASE_AUTH_INVALID_USER)

        } catch (e: FirebaseAuthInvalidCredentialsException) {

            e.printStackTrace()
            Result.Error(FirebaseReauthenticate.FIREBASE_AUTH_INVALID_CREDENTIALS)

        } catch (e: FirebaseNetworkException) {

            e.printStackTrace()
            Result.Error(FirebaseReauthenticate.FIREBASE_NETWORK)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(FirebaseReauthenticate.UNKNOWN)

        }
    }

    override suspend fun reauthenticateWithGoogle(): Result<Unit, FirebaseReauthenticate> {

        when (val result = authenticateWithGoogle()) {

            is Result.Success -> {

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.data.data)
                val lorem = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                return try {

                    Firebase.auth.currentUser?.reauthenticate(lorem)
                    deleteAccount()
                    Result.Success(Unit)

                } catch (e: FirebaseAuthInvalidUserException) {

                    e.printStackTrace()
                    Result.Error(FirebaseReauthenticate.FIREBASE_AUTH_INVALID_USER)

                } catch (e: FirebaseAuthInvalidCredentialsException) {

                    e.printStackTrace()
                    Result.Error(FirebaseReauthenticate.FIREBASE_AUTH_INVALID_CREDENTIALS)

                } catch (e: Exception) {

                    e.printStackTrace()
                    Result.Error(FirebaseReauthenticate.UNKNOWN)

                }
            }

            is Result.Error -> {
                return when (result.error) {
                    GetCredential.GET_CREDENTIAL -> {
                        Result.Error(FirebaseReauthenticate.UNKNOWN)
                    }

                    GetCredential.UNKNOWN -> {
                        Result.Error(FirebaseReauthenticate.UNKNOWN)
                    }
                }
            }
        }

    }

    override suspend fun resetPassword(email: String): Result<Unit, ResetPasswordError> {
        val user = Firebase.auth

        return try {
            user.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            Result.Error(ResetPasswordError.FIREBASE_NETWORK)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ResetPasswordError.UNKNOWN)
        }

    }

    private suspend fun authenticateWithGoogle(): Result<Credential, GetCredential> {
        //Todo: Set Nonce

        val credentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(true)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            Result.Success(result.credential)

        } catch (e: GetCredentialException) {

            e.printStackTrace()
            Result.Error(GetCredential.GET_CREDENTIAL)

        } catch (e: Exception) {

            e.printStackTrace()
            Result.Error(GetCredential.UNKNOWN)

        }
    }

}


sealed interface ProviderId

typealias Provider = ProviderId

enum class FirebaseProviderId : ProviderId {
    PASSWORD,
    GOOGLE,
}