package com.bank.toybank.account

import com.bank.toybank.account.api.AccountCreationRequest
import com.bank.toybank.account.persistence.AccountEntity
import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.account.persistence.AccountType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountFactory: AccountFactory,
    private val accountRepository: AccountRepository
) {

    fun createAccount(accountCreationRequest: AccountCreationRequest): AccountEntity {
        val newAccount = accountFactory.createAccountEntity(accountCreationRequest)
        return accountRepository.save(newAccount)
    }

    fun getAccounts(accountType: List<AccountType>, pageable: Pageable): Page<AccountEntity> =
        if (accountType.isEmpty()) {
            accountRepository.findAll(pageable)
        } else {
            accountRepository.findAllByTypeIsIn(accountType, pageable)
        }

    fun getAccount(iban: String) = accountRepository.findByIban(iban)
}
