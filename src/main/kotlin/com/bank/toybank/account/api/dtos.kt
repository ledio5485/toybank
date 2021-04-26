package com.bank.toybank.account.api

import com.bank.toybank.account.persistence.AccountType
import java.math.BigDecimal

data class AccountCreationRequest(val type: AccountType, val referenceAccount: String? = null)

data class AccountDto(val iban: String, val balance: BigDecimal, val currency: String, val type: AccountType)