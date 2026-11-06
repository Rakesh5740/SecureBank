package com.example.securebank.core.data.repository

import com.example.securebank.core.data.local.dao.BankAccountDao
import com.example.securebank.core.data.local.entity.BankAccountEntity
import com.example.securebank.core.data.local.mapper.toDomain
import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferStatus
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor(
    private val bankAccountDao: BankAccountDao
) : BankRepository {

    override fun observeAccount(accountId: String): Flow<BankAccount?> {
        return bankAccountDao
            .observeAccount(accountId)
            .map { entity ->
                entity?.toDomain()
            }
    }

    override suspend fun addAccount(account: BankAccount) {
        bankAccountDao.insertAccount(
            BankAccountEntity(
                id = account.id,
                accountHolderName = account.accountHolderName,
                accountNumber = account.accountNumber,
                balanceInPaise = account.balanceInPaise
            )
        )
    }

    override suspend fun transferFunds(
        transfer: FundTransfer
    ): Result<FundTransfer> {

        return try {
            val success = bankAccountDao.transferFunds(
                sourceAccountId = transfer.sourceAccountId,
                beneficiaryId = transfer.beneficiaryId,
                amountInPaise = transfer.amountInPaise
            )

            if (success) {
                Result.success(
                    transfer.copy(status = TransferStatus.SUCCESS)
                )
            } else {
                Result.failure(
                    IllegalStateException("Fund transfer failed")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}