package com.bank.toybank.account.api

import com.bank.toybank.account.persistence.AccountEntity

fun AccountEntity.toDto() = with(this) {
    AccountDto(iban = iban, balance = balance, currency= currency, type = type)
}
