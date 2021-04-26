package com.bank.toybank.account.api

import com.bank.toybank.account.api.AccountResource.Companion.ACCOUNTS_URI
import com.bank.toybank.account.persistence.AccountType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.Valid

@RequestMapping(ACCOUNTS_URI)
interface AccountResource {

    companion object {
        private const val IBAN_PATH = "/{iban}"
        const val ACCOUNTS_URI = "/api/accounts"
        const val ACCOUNT_URI = ACCOUNTS_URI.plus(IBAN_PATH)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun createAccount(@RequestBody @Valid accountCreationRequest: AccountCreationRequest): ResponseEntity<AccountDto>

    @GetMapping
    fun getAccounts(@RequestParam("accountType", required = false, defaultValue = "") accountType: List<AccountType> = emptyList(), @PageableDefault pageable: Pageable): ResponseEntity<Page<AccountDto>>

    @GetMapping(IBAN_PATH)
    fun getAccount(@PathVariable("iban") iban: String): ResponseEntity<AccountDto>
}