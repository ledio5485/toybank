package com.bank.toybank.transaction.api

import com.bank.toybank.account.api.AccountCreationRequest
import com.bank.toybank.account.api.AccountDto
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.common.CASH
import com.bank.toybank.common.DEFAULT_CURRENCY
import com.bank.toybank.common.ErrorResponse
import com.bank.toybank.testutil.APIIntegrationTest
import com.bank.toybank.testutil.AbstractIntegrationTest
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.assertj.core.api.Assertions.assertThat
import org.iban4j.Iban
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.math.BigDecimal

internal class TransactionRestControllerIntegrationTest @Autowired constructor(
    private val apiIntegrationTest: APIIntegrationTest, ) : AbstractIntegrationTest() {

    @Test
    internal fun `should return 400 BAD_REQUEST when filter the transactions by an iban of another bank`() {
        val iban = Iban.Builder().buildRandom()

        val actual = apiIntegrationTest.getTransactions<ErrorResponse>(iban.toString())

        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(actual.body?.message).contains("The iban $iban does not belong to our bank, hence it's not possible to extract the transaction history.")
    }

    @Test
    internal fun `should filter transactions by iban`() {
        val depositAmount = BigDecimal("10.00")
        val withdrawAmount = BigDecimal("5.00")
        val transferAmount = BigDecimal("5.00")
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body?: throw Exception("Account creation failed")
        val savingsAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = checkingAccount.iban)).body ?: throw Exception("Account creation failed")

        apiIntegrationTest.deposit<Any>(DepositRequest(to = checkingAccount.iban, amount = depositAmount))
        apiIntegrationTest.withdraw<Any>(WithdrawRequest(from = checkingAccount.iban, amount = withdrawAmount))
        apiIntegrationTest.transfer<Any>(TransferRequest(from = checkingAccount.iban, to = savingsAccount.iban, amount = transferAmount))

        val actual1 = apiIntegrationTest.getTransactions<TransactionPageImpl>(checkingAccount.iban).body!!.content

        val expected1 = listOf(
            TransactionDto(from = CASH, to = checkingAccount.iban, amount = depositAmount, currency = DEFAULT_CURRENCY, timestamp = "not checked"),
            TransactionDto(from = checkingAccount.iban, to = CASH, amount = withdrawAmount, currency = DEFAULT_CURRENCY, timestamp = "not checked"),
            TransactionDto(from = checkingAccount.iban, to = savingsAccount.iban, amount = transferAmount, currency = DEFAULT_CURRENCY, timestamp = "not checked")
        )
        assertThat(actual1).usingElementComparatorIgnoringFields("timestamp").isEqualTo(expected1)

        val actual2 = apiIntegrationTest.getTransactions<TransactionPageImpl>(savingsAccount.iban).body!!.content

        val expected2 = listOf(
            TransactionDto(from = checkingAccount.iban, to = savingsAccount.iban, amount = transferAmount, currency = DEFAULT_CURRENCY, timestamp = "not checked")
        )
        assertThat(actual2).usingElementComparatorIgnoringFields("timestamp").isEqualTo(expected2)
    }
}

class TransactionPageImpl : PageImpl<TransactionDto> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        @JsonProperty("content") content: List<TransactionDto>,
        @JsonProperty("number") number: Int,
        @JsonProperty("size") size: Int,
        @JsonProperty("totalElements") totalElements: Long,
        @JsonProperty("pageable") pageable: JsonNode,
        @JsonProperty("last") last: Boolean,
        @JsonProperty("totalPages") totalPages: Int,
        @JsonProperty("sort") sort: JsonNode,
        @JsonProperty("first") first: Boolean,
        @JsonProperty("numberOfElements") numberOfElements: Int
    ) : super(content, PageRequest.of(number, size), totalElements)
}