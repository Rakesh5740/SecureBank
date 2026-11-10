package com.example.securebank.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.securebank.core.data.local.dao.BankAccountDao
import com.example.securebank.core.data.local.entity.BankAccountEntity
import com.example.securebank.core.data.local.entity.TransferHistoryEntity

@Database(
    entities = [
        BankAccountEntity::class,
        TransferHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SecureBankDatabase : RoomDatabase() {
    abstract fun bankAccountDao(): BankAccountDao
}