package com.guilherme.braintappers.di

import com.guilherme.braintappers.data.FirebaseImpl
import com.guilherme.braintappers.data.TriviaApiServiceImpl
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.presentation.screen.home.HomeViewModel
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailViewModel
import com.guilherme.braintappers.presentation.screen.signup.SignUpViewModel
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

val appModule = module {

    single<FirebaseRepository> { FirebaseImpl() }
    single<TriviaApiService> { TriviaApiServiceImpl() }
    viewModel { SignUpViewModel(get()) }
    viewModel { SignUpWithEmailViewModel(get()) }
    viewModel { SignInWithEmailViewModel(get()) }
    viewModel { HomeViewModel(get()) }

}

fun koinConfiguration() = koinApplication {
    modules(appModule)
}