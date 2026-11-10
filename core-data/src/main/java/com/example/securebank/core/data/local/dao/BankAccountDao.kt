package com.example.securebank.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.securebank.core.data.local.entity.BankAccountEntity
import com.example.securebank.core.data.local.entity.TransferHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountEntity: BankAccountEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccountIfAbsent(accountEntity: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    fun observeAccount(accountId: String): Flow<BankAccountEntity?>

    @Query("SELECT * FROM bank_accounts")
    fun observeAllAccounts(): Flow<List<BankAccountEntity>>

    @Query("UPDATE bank_accounts SET balanceInMinorUnits = :newBalance WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, newBalance: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransferHistory(historyEntity: TransferHistoryEntity)

    @Query("""
    SELECT * FROM transfer_history
    WHERE sourceAccountId = :accountId
       OR beneficiaryId = :accountId
    ORDER BY createdAtEpochMillis DESC
    """)
    fun observeTransferHistory(
        accountId: String
    ): Flow<List<TransferHistoryEntity>>

    @Transaction
    suspend fun transferFunds(
        sourceAccountId: String,
        beneficiaryId: String,
        amountInMinorUnits: Long,
        history: TransferHistoryEntity
    ): Boolean {

        val source = getAccountById(sourceAccountId) ?: return false
        val beneficiary = getAccountById(beneficiaryId) ?: return false

        // Prevent transferring to the same account
        if (sourceAccountId == beneficiaryId) return false

        // Validate transfer amount
        if (amountInMinorUnits <= 0L) return false

        // Both accounts must use the same currency
        if (source.currency != beneficiary.currency) return false

        // History currency must match both accounts
        if (history.currency != source.currency) return false

        // Check sufficient balance
        if (source.balanceInMinorUnits < amountInMinorUnits) return false

        // Prevent Long overflow
        if (beneficiary.balanceInMinorUnits >
            Long.MAX_VALUE - amountInMinorUnits
        ) {
            return false
        }

        updateBalance(
            sourceAccountId,
            source.balanceInMinorUnits - amountInMinorUnits
        )

        updateBalance(
            beneficiaryId,
            beneficiary.balanceInMinorUnits + amountInMinorUnits
        )

        insertTransferHistory(history)

        return true
    }
}