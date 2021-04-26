package com.bank.toybank.common

import java.math.BigDecimal

val CASH: String? = null

data class TransferCommand(val from: String? = CASH, val to: String? = CASH, val amount: BigDecimal)

data class TransactionCommand(val from: String? = CASH, val to: String? = CASH, val amount: BigDecimal)