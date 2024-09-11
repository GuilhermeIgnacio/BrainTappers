package com.guilherme.braintappers.di

import com.guilherme.braintappers.data.FirebaseImpl
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.presentation.screen.SignInWithEmailViewModel
import com.guilherme.braintappers.presentation.screen.SignUpViewModel
import com.guilherme.braintappers.presentation.screen.SignUpWithEmailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

val appModule = module {

    single<FirebaseRepository> { FirebaseImpl() }
    viewModel { SignUpViewModel(get()) }
    viewModel { SignUpWithEmailViewModel(get()) }
    viewModel { SignInWithEmailViewModel(get()) }

}

fun koinConfiguration() = koinApplication {
    modules(appModule)
}