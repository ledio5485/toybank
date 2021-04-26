package com.bank.toybank.transaction

import com.bank.toybank.common.IbanService
import com.bank.toybank.transaction.persistence.TransactionEntity
import com.bank.toybank.transaction.persistence.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class TransactionQueryService(
    private val transactionRepository: TransactionRepository,
    private val ibanService: IbanService
) {
    fun getTransactions(iban: String, pageable: Pageable): Page<TransactionEntity> {
        require(ibanService.belongsToOurBank(iban)) { "The iban $iban does not belong to our bank, hence it's not possible to extract the transaction history." }

        return transactionRepository.findAllByFromAccountEqualsOrToAccountEquals(iban, iban, pageable)
    }
}