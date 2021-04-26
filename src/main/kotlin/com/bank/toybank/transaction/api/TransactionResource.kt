package com.bank.toybank.transaction.api

import com.bank.toybank.transaction.api.TransactionResource.Companion.TRANSACTIONS_URI
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping(TRANSACTIONS_URI)
interface TransactionResource {
    companion object {
        const val TRANSACTIONS_URI = "/api/transactions"
    }

    @GetMapping
    fun getTransactions(
        @RequestParam("iban", required = false, defaultValue = "") iban: String,
        @PageableDefault pageable: Pageable
    ): ResponseEntity<Page<TransactionDto>>
}