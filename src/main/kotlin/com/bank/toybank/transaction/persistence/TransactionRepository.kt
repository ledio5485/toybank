package com.bank.toybank.transaction.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {

    fun findAllByFromAccountEqualsOrToAccountEquals(fromAccount: String, toAccount: String, pageable: Pageable): Page<TransactionEntity>
}