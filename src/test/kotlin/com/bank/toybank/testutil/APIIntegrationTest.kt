package com.bank.toybank.testutil

import com.bank.toybank.account.api.AccountCreationRequest
import com.bank.toybank.account.api.AccountResource.Companion.ACCOUNTS_URI
import com.bank.toybank.account.api.AccountResource.Companion.ACCOUNT_URI
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.transaction.api.DepositRequest
import com.bank.toybank.transaction.api.TransactionResource.Companion.TRANSACTIONS_URI
import com.bank.toybank.transaction.api.TransferRequest
import com.bank.toybank.transaction.api.TransferResource.Companion.DEPOSIT_URI
import com.bank.toybank.transaction.api.TransferResource.Companion.TRANSFER_URI
import com.bank.toybank.transaction.api.TransferResource.Companion.WITHDRAW_URI
import com.bank.toybank.transaction.api.WithdrawRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component


@Component
internal class APIIntegrationTest @Autowired constructor(private val restTemplate: TestRestTemplate) {

    final inline fun <reified T> createAccount(accountCreationRequest: AccountCreationRequest): ResponseEntity<T> =
        restTemplate.postForEntity(ACCOUNTS_URI, accountCreationRequest, T::class.java)

    final inline fun <reified T> getAccounts(accountType: List<AccountType> = emptyList()): ResponseEntity<T> =
        restTemplate.getForEntity("$ACCOUNTS_URI?accountType=${accountType.joinToString()}", T::class.java)

    final inline fun <reified T> getAccount(iban: String): ResponseEntity<T> =
        restTemplate.getForEntity(ACCOUNT_URI, T::class.java, iban)

    final inline fun <reified T> deposit(request: DepositRequest): ResponseEntity<T> =
        restTemplate.postForEntity(DEPOSIT_URI, request, T::class.java)

    final inline fun <reified T> withdraw(request: WithdrawRequest): ResponseEntity<T> =
        restTemplate.postForEntity(WITHDRAW_URI, request, T::class.java)

    final inline fun <reified T> transfer(request: TransferRequest): ResponseEntity<T> =
        restTemplate.postForEntity(TRANSFER_URI, request, T::class.java)

    final inline fun <reified T> getTransactions(iban: String): ResponseEntity<T> =
        restTemplate.getForEntity("$TRANSACTIONS_URI?iban=$iban", T::class.java)
}