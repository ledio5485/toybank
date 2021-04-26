package com.bank.toybank.transaction.api

import com.bank.toybank.transaction.persistence.TransactionEntity

fun TransactionEntity.toDto() = with(this) {
    TransactionDto(from = fromAccount, to = toAccount, amount = amount, currency = currency, timestamp = createdAt.toString())
}