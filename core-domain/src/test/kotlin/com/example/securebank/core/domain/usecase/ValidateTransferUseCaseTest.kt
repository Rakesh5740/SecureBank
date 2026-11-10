package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class ValidateTransferUseCaseTest {

    private lateinit var useCase: ValidateTransferUseCase
    private lateinit var account: BankAccount

    @Before
    fun setUp() {
        useCase = ValidateTransferUseCase()

        account = BankAccount(
            id = "ACC-001",
            accountHolderName = "Test User",
            accountNumber = "1234567890",
            balanceInMinorUnits = 500_000L
        )
    }

    @Test
    fun `zero amount returns invalid amount`() {
        val result = useCase(
            sourceAccount = account,
            beneficiaryId = "ACC-002",
            amountInPaise = 0L
        )

        assertEquals(
            TransferValidationResult.Reason.INVALID_AMOUNT,
            (result as TransferValidationResult.Invalid).reason
        )
    }

    @Test
    fun `amount greater then balance return insufficient balance`() {
        val result = useCase(
            sourceAccount = account,
            beneficiaryId = "ACC-002",
            amountInPaise = 600_000L
        )
        assertEquals(
            TransferValidationResult.Reason.INSUFFICIENT_BALANCE,
            (result as TransferValidationResult.Invalid).reason
        )
    }

    @Test
    fun `same account returns validation error`() {
        val result = useCase(
            sourceAccount = account,
            beneficiaryId = "ACC-001",
            amountInPaise = 1000_000L
        )
        assertEquals(
            TransferValidationResult.Reason.SAME_ACCOUNT,
            (result as TransferValidationResult.Invalid).reason
        )
    }

    @Test
    fun `valid transfer returns valid result`() {
        val result = useCase(
            sourceAccount = account,
            beneficiaryId = "ACC-002",
            amountInPaise = 100_000L // ₹1,000
        )
        assertTrue(result is TransferValidationResult.Valid)
    }
}