package com.bank.toybank.transaction.api

import com.bank.toybank.transaction.api.TransferResource.Companion.TRANSFERS_URI
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.Valid

@RequestMapping(TRANSFERS_URI)
interface TransferResource {
    companion object {
        private const val DEPOSIT = "/deposit"
        private const val WITHDRAW = "/withdraw"
        private const val TRANSFER = "/transfer"
        const val TRANSFERS_URI = "/api/transfers"
        const val DEPOSIT_URI = TRANSFERS_URI.plus(DEPOSIT)
        const val WITHDRAW_URI = TRANSFERS_URI.plus(WITHDRAW)
        const val TRANSFER_URI = TRANSFERS_URI.plus(TRANSFER)
    }

    @PostMapping(DEPOSIT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deposit(@RequestBody @Valid request: DepositRequest)

    @PostMapping(WITHDRAW)
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun withdraw(@RequestBody @Valid request: WithdrawRequest)

    @PostMapping(TRANSFER)
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun transfer(@RequestBody @Valid request: TransferRequest)
}