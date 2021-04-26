package com.bank.toybank.transaction.persistence

import com.bank.toybank.common.DEFAULT_CURRENCY
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import java.time.Clock
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TRANSACTION")
data class TransactionEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column(updatable = false)
    val fromAccount: String? = null,

    @Column(updatable = false)
    val toAccount: String? = null,

    @Column(updatable = false, nullable = false)
    val amount: BigDecimal,

    @Column(updatable = false, nullable = false)
    val currency: String = DEFAULT_CURRENCY,

    @Column(updatable = false, nullable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)