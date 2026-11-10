
package com.example.securebank.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `transfer_history` (
                `id` TEXT NOT NULL,
                `sourceAccountId` TEXT NOT NULL,
                `beneficiaryId` TEXT NOT NULL,
                `amountInPaise` INTEGER NOT NULL,
                `status` TEXT NOT NULL,
                `createdAtEpochMillis` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE bank_accounts
            RENAME COLUMN balanceInPaise TO balanceInMinorUnits
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE bank_accounts
            ADD COLUMN currency TEXT NOT NULL DEFAULT 'INR'
            """.trimIndent()
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            ALTER TABLE transfer_history
            RENAME COLUMN amountInPaise TO amountInMinorUnits
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE transfer_history
            ADD COLUMN currency TEXT NOT NULL DEFAULT 'INR'
            """.trimIndent()
        )
    }
}

