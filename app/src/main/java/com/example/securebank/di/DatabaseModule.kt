package com.example.securebank.di

import android.content.Context
import androidx.room.Room
import com.example.securebank.core.data.local.MIGRATION_1_2
import com.example.securebank.core.data.local.MIGRATION_2_3
import com.example.securebank.core.data.local.MIGRATION_3_4
import com.example.securebank.core.data.local.SecureBankDatabase
import com.example.securebank.core.data.local.dao.BankAccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SecureBankDatabase {
        return Room.databaseBuilder(
            context,
            SecureBankDatabase::class.java,
            "secure_bank_database"
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideBankAccountDao(
        database: SecureBankDatabase
    ): BankAccountDao {
        return database.bankAccountDao()
    }

}