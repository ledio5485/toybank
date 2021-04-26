package com.bank.toybank.account.api

import com.bank.toybank.testutil.APIIntegrationTest
import com.bank.toybank.testutil.AbstractIntegrationTest
import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.common.ErrorResponse
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus

internal class AccountRestControllerIntegrationTest @Autowired constructor(
    private val apiIntegrationTest: APIIntegrationTest,
    private val accountRepository: AccountRepository
) : AbstractIntegrationTest() {

    @Test
    internal fun `should create CHECKING account`() {
        val actual = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING))

        val expected = accountRepository.findByIban(actual.body!!.iban).orElseThrow().toDto()
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(actual.body).isEqualTo(expected)
    }

    @Test
    internal fun `should create LOAN account`() {
        val actual = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.LOAN))

        val expected = accountRepository.findByIban(actual.body!!.iban).orElseThrow().toDto()
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(actual.body).isEqualTo(expected)
    }

    @Test
    internal fun `should return 400 BAD_REQUEST and not create SAVINGS account if referenceAccount is not specified`() {
        val actual = apiIntegrationTest.createAccount<ErrorResponse>(AccountCreationRequest(type = AccountType.SAVINGS))

        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    internal fun `should return 400 BAD_REQUEST and not create SAVINGS account if referenceAccount does not exist`() {
        val actual = apiIntegrationTest.createAccount<ErrorResponse>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = "DE61000700070000000000"))

        assertThat(actual.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    internal fun `should create SAVINGS account`() {
        val referenceAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body!!

        val actual = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.SAVINGS, referenceAccount = referenceAccount.iban))

        val expected = accountRepository.findByIban(actual.body!!.iban).orElseThrow().toDto()
        assertThat(actual.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(actual.body).isEqualTo(expected)
    }

    @Test
    internal fun `should get paginated accounts`() {
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body
        val loanAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.LOAN)).body

        val actual = apiIntegrationTest.getAccounts<AccountPageImpl>().body!!.content

        val expected = listOf(checkingAccount, loanAccount)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should get paginated accounts filtered by accountType`() {
        val checkingAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body
        val loanAccount = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.LOAN)).body

        val actual = apiIntegrationTest.getAccounts<AccountPageImpl>(accountType = listOf(AccountType.CHECKING)).body!!.content

        val expected = listOf(checkingAccount)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should return 404 NOT_FOUND when getting an account by iban that does not exist`() {
        val actual = apiIntegrationTest.getAccount<ErrorResponse>("DE61000700070000000000")

        assertThat(actual.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    internal fun `should get account by iban`() {
        val expected = apiIntegrationTest.createAccount<AccountDto>(AccountCreationRequest(type = AccountType.CHECKING)).body!!

        val actual = apiIntegrationTest.getAccount<AccountDto>(expected.iban).body

        assertThat(actual).isEqualTo(expected)
    }
}

class AccountPageImpl : PageImpl<AccountDto> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        @JsonProperty("content") content: List<AccountDto>,
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