package com.bank.toybank.account.api

import com.bank.toybank.account.AccountService
import com.bank.toybank.account.persistence.AccountType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountRestController(private val accountService: AccountService) : AccountResource {

    override fun createAccount(accountCreationRequest: AccountCreationRequest) =
        with(accountService.createAccount(accountCreationRequest)) {
            ResponseEntity.status(HttpStatus.CREATED).body(toDto())
        }

    override fun getAccounts(accountType: List<AccountType>, pageable: Pageable): ResponseEntity<Page<AccountDto>> =
        with(accountService.getAccounts(accountType, pageable)) {
            ResponseEntity.ok(PageImpl(this.content.map { it.toDto() }, this.pageable, this.totalElements))
        }

    override fun getAccount(iban: String): ResponseEntity<AccountDto> =
        with(accountService.getAccount(iban).map { it.toDto() }) {
            ResponseEntity.of(this)
        }
}