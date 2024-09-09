package com.guilherme.braintappers.di

import com.guilherme.braintappers.data.FirebaseImpl
import com.guilherme.braintappers.domain.FirebaseRepository
import com.guilherme.braintappers.presentation.screen.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module

val appModule = module {

    single<FirebaseRepository> { FirebaseImpl() }
    viewModel { SignUpViewModel(get()) }

}

fun koinConfiguration() = koinApplication {
    modules(appModule)
}