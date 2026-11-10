package com.example.securebank.core.data.repository

import com.example.securebank.core.data.local.dao.BankAccountDao
import com.example.securebank.core.data.local.entity.BankAccountEntity
import com.example.securebank.core.data.local.entity.TransferHistoryEntity
import com.example.securebank.core.data.local.mapper.toDomain as localToDomain
import com.example.securebank.core.data.remote.BankApiService
import com.example.securebank.core.data.remote.mapper.toDomain as remoteToDomain
import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferHistory
import com.example.securebank.core.domain.model.TransferStatus
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor(
    private val bankAccountDao: BankAccountDao,
    private val bankApiService: BankApiService
) : BankRepository {

    override fun observeAccount(accountId: String): Flow<BankAccount?> {
        return bankAccountDao.observeAccount(accountId)
            .map { entity -> entity?.localToDomain() }
    }

    override suspend fun addAccount(account: BankAccount) {
        bankAccountDao.insertAccount(
            BankAccountEntity(
                id = account.id,
                accountHolderName = account.accountHolderName,
                accountNumber = account.accountNumber,
                balanceInMinorUnits = account.balanceInMinorUnits,
                currency = account.currency
            )
        )
    }

    override suspend fun fetchRemoteAccounts(): Result<List<BankAccount>> {
        return try {
            val response = bankApiService.getAccounts()
            val accounts = response.data.map { dto -> dto.remoteToDomain() }

            accounts.forEach { account ->
                bankAccountDao.insertAccountIfAbsent(
                    BankAccountEntity(
                        id = account.id,
                        accountHolderName = account.accountHolderName,
                        accountNumber = account.accountNumber,
                        balanceInMinorUnits = account.balanceInMinorUnits,
                        currency = account.currency
                    )
                )
            }

            Result.success(accounts)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun transferFunds(
        transfer: FundTransfer
    ): Result<FundTransfer> {
        return try {
            if (transfer.amountInMinorUnits <= 0L) {
                return Result.failure(
                    IllegalArgumentException("Transfer amount must be greater than zero")
                )
            }

            val sourceAccount = bankAccountDao
                .observeAccount(transfer.sourceAccountId)
                .first()
                ?: return Result.failure(
                    IllegalArgumentException("Source account not found")
                )

            if (sourceAccount.currency != transfer.currency) {
                return Result.failure(
                    IllegalArgumentException(
                        "Transfer currency does not match source account currency"
                    )
                )
            }

            val history = TransferHistoryEntity(
                id = transfer.id,
                sourceAccountId = transfer.sourceAccountId,
                beneficiaryId = transfer.beneficiaryId,
                amountInMinorUnits = transfer.amountInMinorUnits,
                currency = transfer.currency,
                status = TransferStatus.SUCCESS.name,
                createdAtEpochMillis = System.currentTimeMillis()
            )

            val success = bankAccountDao.transferFunds(
                sourceAccountId = transfer.sourceAccountId,
                beneficiaryId = transfer.beneficiaryId,
                amountInMinorUnits = transfer.amountInMinorUnits,
                history = history
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransferHistory(
        accountId: String
    ): Flow<List<TransferHistory>> {
        return bankAccountDao.observeTransferHistory(accountId)
            .map { entities ->
                entities.map { entity -> entity.localToDomain() }
            }
    }
}
