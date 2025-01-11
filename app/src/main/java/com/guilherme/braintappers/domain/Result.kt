package com.guilherme.braintappers.domain

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

typealias RootError = Error

sealed interface Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D : Any, out E : RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Result<D, E>
}

@Composable
fun <D, E : RootError> Result<D, E>.DisplayResult(
    onIdle: (@Composable () -> Unit)? = null,
    onLoading: (@Composable () -> Unit)? = null,
    onError: (@Composable (E) -> Unit)? = null,
    onSuccess: (@Composable (D) -> Unit)? = null,
    transitionSpec: ContentTransform = scaleIn(tween(durationMillis = 400))
            + fadeIn(tween(durationMillis = 800))
            togetherWith scaleOut(tween(durationMillis = 400))
            + fadeOut(tween(durationMillis = 800))
) {
    AnimatedContent(
        targetState = this,
        transitionSpec = { transitionSpec },
        label = "ContentAnimation"
    ) { state ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (state) {
                is Result.Success -> {
                    onSuccess?.invoke(state.data)
                }

                is Result.Error -> {
                    onError?.invoke(state.error)
                }
            }
        }
    }
}

enum class DataError: Error {
    UNKNOWN
}

enum class FirebaseEmailAndPasswordAuthError : Error {
    UNKNOWN,
    FIREBASE_AUTH_USER_COLLISION,
    FIREBASE_NETWORK
}

enum class FirebaseGoogleAuthError: Error {
    UNKNOWN,
    FIREBASE_AUTH_INVALID_USER,
    FIREBASE_AUTH_INVALID_CREDENTIALS,
    FIREBASE_AUTH_USER_COLLISION,
}

enum class FirebaseSignInWithEmailAndPasswordError: Error {
    UNKNOWN,
    FIREBASE_AUTH_INVALID_CREDENTIALS,
    FIREBASE_NETWORK
}

enum class FirebaseAccountDeletion: Error {
    FIREBASE_AUTH_INVALID_USER,
    FIREBASE_AUTH_RECENT_LOGIN_REQUIRED,
    FIREBASE_NETWORK,
    UNKNOWN,
}

enum class FirebaseCurrentUser: Error {
    NULL_VALUE,
    UNEXPECTED_PROVIDER,
    UNKNOWN
}

enum class FirebaseReauthenticate: Error {
    FIREBASE_AUTH_INVALID_USER,
    FIREBASE_AUTH_INVALID_CREDENTIALS,
    FIREBASE_NETWORK,
    UNKNOWN
}

enum class GetCredential: Error {
    GET_CREDENTIAL,
    UNKNOWN
}

enum class FirestoreError: Error{
    UNKNOWN,
    FIREBASE_NETWORK
}

enum class FirebaseGetUserQuizzes: Error {
    UNKNOWN
}

enum class FirebaseFirestoreDeleteError: Error {
    UNKNOWN,
    FIREBASE_NETWORK
}

enum class ResetPasswordError: Error {
    FIREBASE_NETWORK,
    UNKNOWN
}

enum class LinkAccountWithEmailError: Error {
    FIREBASE_AUTH_WEAK_PASSWORD,
    FIREBASE_AUTH_INVALID_CREDENTIALS,
    FIREBASE_AUTH_USER_COLLISION,
    FIREBASE_AUTH_INVALID_USER,
    FIREBASE_AUTH,
    FIREBASE_NETWORK,
    UNKNOWN
}

enum class LinkAccountWithGoogleError:Error {
    FIREBASE_AUTH_WEAK_PASSWORD,
    FIREBASE_AUTH_INVALID_CREDENTIALS,
    FIREBASE_AUTH_USER_COLLISION,
    FIREBASE_AUTH_INVALID_USER,
    FIREBASE_AUTH,
    FIREBASE_NETWORK,
    GET_CREDENTIAL,
    UNKNOWN,
}