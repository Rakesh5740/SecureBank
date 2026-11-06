
package com.example.securebank.di

import com.example.securebank.core.domain.repository.BankRepository
import com.example.securebank.core.domain.usecase.GetAccountUseCase
import com.example.securebank.core.domain.usecase.SeedDemoAccountsUseCase
import com.example.securebank.core.domain.usecase.TransferFundsUseCase
import com.example.securebank.core.domain.usecase.ValidateTransferUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAccountUseCase(
        repository: BankRepository
    ): GetAccountUseCase {
        return GetAccountUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideValidateTransferUseCase(): ValidateTransferUseCase {
        return ValidateTransferUseCase()
    }

    @Provides
    @Singleton
    fun provideTransferFundsUseCase(
        getAccountUseCase: GetAccountUseCase,
        validateTransferUseCase: ValidateTransferUseCase,
        repository: BankRepository
    ): TransferFundsUseCase {
        return TransferFundsUseCase(
            getAccountUseCase,
            validateTransferUseCase,
            repository
        )
    }

    @Provides
    fun provideSeedDemoAccountsUseCase(
        repository: BankRepository
    ): SeedDemoAccountsUseCase {
        return SeedDemoAccountsUseCase(repository)
    }
}
