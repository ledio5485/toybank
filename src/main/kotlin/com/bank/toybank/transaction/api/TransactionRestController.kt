package com.bank.toybank.transaction.api

import com.bank.toybank.transaction.TransactionQueryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TransactionRestController(private val transactionQueryService: TransactionQueryService) : TransactionResource {

    override fun getTransactions(iban: String, pageable: Pageable): ResponseEntity<Page<TransactionDto>> =
        with(transactionQueryService.getTransactions(iban, pageable)) {
            ResponseEntity.ok(PageImpl(this.content.map { it.toDto() }, this.pageable, this.totalElements))
        }
}