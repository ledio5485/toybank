package com.bank.toybank.account

import com.bank.toybank.account.api.AccountCreationRequest
import com.bank.toybank.account.persistence.AccountEntity
import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.common.IbanService
import org.springframework.stereotype.Component

@Component
class AccountFactory(private val ibanService: IbanService, private val accountRepository: AccountRepository) {

    fun createAccountEntity(accountCreationRequest: AccountCreationRequest): AccountEntity {
        return when (accountCreationRequest.type) {
            AccountType.CHECKING, AccountType.LOAN -> createAccount(accountCreationRequest.type)
            AccountType.SAVINGS -> createAccount(
                type = accountCreationRequest.type,
                referenceAccount = accountCreationRequest.referenceAccount
            )
        }
    }

    private fun createAccount(type: AccountType = AccountType.CHECKING) =
        AccountEntity(iban = ibanService.generate().toString(), type = type, referenceAccount = null)

    private fun createAccount(type: AccountType = AccountType.CHECKING, referenceAccount: String?) =
        AccountEntity(iban = ibanService.generate().toString(), type = type, referenceAccount = getReferenceAccount(referenceAccount))

    private fun getReferenceAccount(referenceAccount: String?): AccountEntity {
        requireNotNull(referenceAccount) { "The 'Reference Account' is mandatory when creating a SAVINGS account." }
            .also {
                require(ibanService.belongsToOurBank(referenceAccount)) { "The reference account should belong to our bank." }
            }

        return accountRepository.findByIban(referenceAccount)
            .filter { it.type == AccountType.CHECKING }
            .orElseThrow { IllegalArgumentException("The account reference should exist and it should be CHECKING account.") }
    }
}