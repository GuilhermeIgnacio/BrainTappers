package com.guilherme.braintappers.di

import android.content.Context
import com.guilherme.braintappers.MainViewModel
import com.guilherme.braintappers.data.FirebaseFirestoreImpl
import com.guilherme.braintappers.data.FirebaseImpl
import com.guilherme.braintappers.data.TriviaApiServiceImpl
import com.guilherme.braintappers.domain.FirebaseFirestoreRepository
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.domain.TriviaApiService
import com.guilherme.braintappers.presentation.screen.profile.ProfileViewModel
import com.guilherme.braintappers.presentation.screen.quizzesplayed.QuizzesPlayedViewModel
import com.guilherme.braintappers.presentation.screen.signin.SignInViewModel
import com.guilherme.braintappers.presentation.screen.triviasettings.TriviaSettingsViewModel
import com.guilherme.braintappers.presentation.screen.signin.signinwithemail.SignInWithEmailViewModel
import com.guilherme.braintappers.presentation.screen.signup.SignUpViewModel
import com.guilherme.braintappers.presentation.screen.signup.signupwithemail.SignUpWithEmailViewModel
import com.guilherme.braintappers.presentation.screen.trivia.TriviaMainViewModel
import com.guilherme.braintappers.presentation.screen.welcome.WelcomeScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module



fun koinConfiguration(context: Context) = koinApplication {

    val appModule = module {

        single<FirebaseRepository> { FirebaseImpl(context) }
        single<FirebaseFirestoreRepository> { FirebaseFirestoreImpl() }
        single<TriviaApiService> { TriviaApiServiceImpl() }
        viewModel { SignUpViewModel(get()) }
        viewModel { SignUpWithEmailViewModel(get()) }
        viewModel { SignInWithEmailViewModel(get()) }
        viewModel { TriviaSettingsViewModel() }
        viewModel { TriviaMainViewModel(get(), get()) }
        viewModel { WelcomeScreenViewModel(get()) }
        viewModel { MainViewModel(get()) }
        viewModel { SignInViewModel(get()) }
        viewModel { ProfileViewModel(get(), get()) }
        viewModel { QuizzesPlayedViewModel(get()) }

    }

    modules(appModule)
}