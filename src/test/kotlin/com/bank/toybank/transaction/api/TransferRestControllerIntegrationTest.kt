package com.bank.toybank.transaction.api

import com.bank.toybank.testutil.APIIntegrationTest
import com.bank.toybank.testutil.AbstractIntegrationTest
import com.bank.toybank.account.api.AccountCreationRequest
import com.bank.toybank.account.api.AccountDto
import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.common.DEFAULT_CURRENCY
import com.bank.toybank.transaction.persistence.TransactionEntity
import com.bank.toybank.transaction.persistence.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

internal class TransferRestControllerIntegrationTest @Autowired constructor(
    private val apiIntegrationTest: APIIntegrationTest,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : AbstractIntegrationTest() {

    @Test
    internal fun `should allow deposit for all accounts`() {
        val depositAmount = BigDecimal("10.00")
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body ?: throw Exception("Account creation failed")
        val savingsAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = checkingAccount.iban)).body ?: throw Exception("Account creation failed")
        val loanAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.LOAN)).body ?: throw Exception("Account creation failed")

        listOf(checkingAccount, savingsAccount, loanAccount).forEach { account ->
            apiIntegrationTest.deposit<Any>(DepositRequest(to = account.iban, amount = depositAmount))

            val balance = accountRepository.findByIban(account.iban).orElseThrow().balance
            assertThat(balance).isEqualTo(depositAmount)

            with(transactionRepository.findAll().filter { it.toAccount == account.iban }) {
                assertThat(this.size).isEqualTo(1)
                val txExpected = listOf(TransactionEntity(toAccount = account.iban, amount = depositAmount, currency = DEFAULT_CURRENCY))
                assertThat(this).usingRecursiveComparison().ignoringFields("id", "createdAt").isEqualTo(txExpected)
            }
        }
    }

    @Test
    internal fun `should allow withdrawal for CHECKING accounts only`() {
        val depositAmount = BigDecimal("10.00")
        val withdrawalAmount = BigDecimal("5.00")
        val account = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body ?: throw Exception("Account creation failed")

        apiIntegrationTest.deposit<Any>(DepositRequest(to = account.iban, amount = depositAmount))
        apiIntegrationTest.withdraw<Any>(WithdrawRequest(from = account.iban, amount = withdrawalAmount))

        val balance = accountRepository.findByIban(account.iban).orElseThrow().balance
        assertThat(balance).isEqualTo(depositAmount.minus(withdrawalAmount))

        with(
            transactionRepository.findAll().filter { it.toAccount == account.iban || it.fromAccount == account.iban }) {
            assertThat(this.size).isEqualTo(2)
            val txExpected = listOf(
                TransactionEntity(toAccount = account.iban, amount = depositAmount, currency = DEFAULT_CURRENCY),
                TransactionEntity(fromAccount = account.iban, amount = withdrawalAmount, currency = DEFAULT_CURRENCY)
            )
            assertThat(this).usingRecursiveComparison().ignoringFields("id", "createdAt").isEqualTo(txExpected)
        }
    }

    @Test
    internal fun `should not allow withdrawal for SAVINGS and LOAN accounts `() {
        val depositAmount = BigDecimal("10.00")
        val withdrawalAmount = BigDecimal("5.00")
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body?: throw Exception("Account creation failed")
        val savingsAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = checkingAccount.iban)).body ?: throw Exception("Account creation failed")
        val loanAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.LOAN)).body ?: throw Exception("Account creation failed")

        listOf(savingsAccount, loanAccount).forEach { account ->
            apiIntegrationTest.deposit<Any>(DepositRequest(to = account.iban, amount = depositAmount))
            apiIntegrationTest.withdraw<Any>(WithdrawRequest(from = account.iban, amount = withdrawalAmount))

            val balance = accountRepository.findByIban(account.iban).orElseThrow().balance
            assertThat(balance).isEqualTo(depositAmount)

            with(
                transactionRepository.findAll().filter { it.toAccount == account.iban || it.fromAccount == account.iban }) {
                assertThat(this.size).isEqualTo(1)
                val txExpected = listOf(TransactionEntity(toAccount = account.iban, amount = depositAmount, currency = DEFAULT_CURRENCY),)
                assertThat(this).usingRecursiveComparison().ignoringFields("id", "createdAt").isEqualTo(txExpected)
            }
        }
    }

    @Test
    internal fun `should do a transfer if the balance is positive and remains still after the transfer`() {
        val depositAmount = BigDecimal("10.00")
        val transferAmount = BigDecimal("5.00")
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body?: throw Exception("Account creation failed")
        val savingsAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = checkingAccount.iban)).body ?: throw Exception("Account creation failed")

        apiIntegrationTest.deposit<Any>(DepositRequest(to = checkingAccount.iban, amount = depositAmount))
        apiIntegrationTest.transfer<Any>(TransferRequest(from = checkingAccount.iban, to = savingsAccount.iban, amount = transferAmount))

        val savingsAccountBalance = accountRepository.findByIban(savingsAccount.iban).orElseThrow().balance
        assertThat(savingsAccountBalance).isEqualTo(transferAmount)

        val checkingAccountBalance = accountRepository.findByIban(checkingAccount.iban).orElseThrow().balance
        assertThat(checkingAccountBalance).isEqualTo(depositAmount.minus(transferAmount))
    }

    @Test
    internal fun `should not allow the overdraft if the balance becomes negative after the transfer`() {
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body?: throw Exception("Account creation failed")
        val savingsAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = checkingAccount.iban)).body ?: throw Exception("Account creation failed")

        apiIntegrationTest.transfer<Any>(TransferRequest(from = checkingAccount.iban, to = savingsAccount.iban, amount = BigDecimal.TEN))

        val savingsAccountBalance = accountRepository.findByIban(savingsAccount.iban).orElseThrow().balance
        assertThat(savingsAccountBalance).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.ZERO)

        val checkingAccountBalance = accountRepository.findByIban(checkingAccount.iban).orElseThrow().balance
        assertThat(checkingAccountBalance).usingComparator(BigDecimal::compareTo).isEqualTo(BigDecimal.ZERO)
    }
}
