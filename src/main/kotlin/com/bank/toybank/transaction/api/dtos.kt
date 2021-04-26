package com.bank.toybank.transaction.api

import java.math.BigDecimal
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class DepositRequest(
    @field:NotBlank
    val to: String,
    @field:Positive
    val amount: BigDecimal
)

data class WithdrawRequest(
    @field:NotBlank
    val from: String,
    @field:Positive val amount: BigDecimal
)

data class TransferRequest(
    @field:NotBlank
    val from: String,
    @field:NotBlank
    val to: String,
    @field:Positive val amount: BigDecimal
)

data class TransactionDto(
    val from: String?,
    val to: String?,
    val amount: BigDecimal,
    val currency: String,
    val timestamp: String
)