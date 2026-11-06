package com.example.securebank.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.securebank.core.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountEntity: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE id = :accountId")
    fun observeAccount(accountId: String): Flow<BankAccountEntity?>

    @Query("SELECT * FROM bank_accounts")
    fun observeAllAccounts(): Flow<List<BankAccountEntity>>

    @Query("UPDATE bank_accounts SET balanceInPaise = :newBalance WHERE id = :accountId")
    suspend fun updateBalance(accountId: String, newBalance: Long)

    @androidx.room.Transaction
    suspend fun transferFunds(
        sourceAccountId: String,
        beneficiaryId: String,
        amountInPaise: Long
    ): Boolean {
        val source = getAccountById(sourceAccountId)
        val beneficiary = getAccountById(beneficiaryId)

        if (sourceAccountId == beneficiaryId) return false
        if (source != null) {
            if (amountInPaise <= 0 || source.balanceInPaise < amountInPaise) {
                return false
            }
        }

        if (source != null) {
            updateBalance(
                sourceAccountId,
                source.balanceInPaise - amountInPaise
            )
        }

        if (beneficiary != null) {
            updateBalance(
                beneficiaryId,
                beneficiary.balanceInPaise + amountInPaise
            )
        }
        return true
    }
}