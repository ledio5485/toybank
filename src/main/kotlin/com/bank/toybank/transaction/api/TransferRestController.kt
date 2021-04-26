package com.bank.toybank.transaction.api

import com.bank.toybank.common.TransferCommand
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransferRestController(private val applicationEventPublisher: ApplicationEventPublisher): TransferResource {

    override fun deposit(@RequestBody request: DepositRequest) = beginTransaction(TransferCommand(to = request.to, amount = request.amount))

    override fun withdraw(@RequestBody request: WithdrawRequest) = beginTransaction(TransferCommand(from = request.from, amount = request.amount))

    override fun transfer(@RequestBody request: TransferRequest) = beginTransaction(TransferCommand(from = request.from, to = request.to, amount = request.amount))

    private fun beginTransaction(command: TransferCommand) = applicationEventPublisher.publishEvent(command)
}
