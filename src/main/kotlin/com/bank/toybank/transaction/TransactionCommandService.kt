package com.bank.toybank.transaction

import com.bank.toybank.common.TransactionCommand
import com.bank.toybank.common.TransferCommand
import com.bank.toybank.transaction.persistence.TransactionEntity
import com.bank.toybank.transaction.persistence.TransactionRepository
import com.bank.toybank.transaction.policies.PoliciesValidationService
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
@Async
class TransactionCommandService(
    private val policiesValidationService: PoliciesValidationService,
    private val transactionRepository: TransactionRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @EventListener
    fun processTransferRequest(command: TransferCommand): TransactionEntity {
        logger.info { "Received TransferCommand $command" }

        val tx = with(command) { TransactionEntity(toAccount = to, fromAccount = from, amount = amount) }
        policiesValidationService.apply(tx)

        val savedTx = transactionRepository.save(tx)

        val txCommand = with(savedTx) { TransactionCommand(to = toAccount, from = fromAccount, amount = amount) }
        applicationEventPublisher.publishEvent(txCommand)

        return savedTx
    }
}