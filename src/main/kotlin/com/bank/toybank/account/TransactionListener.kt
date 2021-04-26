package com.bank.toybank.account

import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.common.TransactionCommand
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

@Component
class TransactionListener(private val accountRepository: AccountRepository) {

    @EventListener
    @Transactional
    fun receivedTransactionCommand(command: TransactionCommand) = with(command) {
        logger.info { "Received TransactionCommand: $command" }
        deposit(from, amount.negate())
        deposit(to, amount)
    }

    private fun deposit(iban: String?, amount: BigDecimal) = run {
        if (iban != null) {
            accountRepository.findByIban(iban).ifPresent { account ->
                run {
                    account.balance += amount
                    accountRepository.save(account)
                }
            }
        }
    }
}