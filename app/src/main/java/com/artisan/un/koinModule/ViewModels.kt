package com.artisan.un.koinModule

import com.artisan.un.baseClasses.CommonViewModel
import com.artisan.un.ui.home.viewModel.*
import com.artisan.un.ui.product.viewModel.*
import com.artisan.un.ui.userauth.viewModel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myViewModel = module {
    viewModel { CommonViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { OTPViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { ResetPasswordViewModel(get()) }
    viewModel { DocumentUploadViewModel(get()) }
    viewModel { EditAddressViewModel(get()) }
    viewModel { ProductAddBasicViewModel(get()) }
    viewModel { ProductAddDescViewModel(get()) }
    viewModel { DraftViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProductionDescriptionViewModel(get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
}

