package com.bank.toybank.account.persistence

import com.bank.toybank.common.DEFAULT_CURRENCY
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import java.math.BigDecimal
import java.time.Clock
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.Positive

@Entity
@Table(name = "ACCOUNT")
@DynamicUpdate
//@Where(clause = "locked=false")
data class AccountEntity(

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @OneToOne(optional = true)
    @JoinColumn(name="ID", referencedColumnName="ID", nullable=true)
    val referenceAccount: AccountEntity? = null,

    @Column(nullable = false)
    val iban: String,

    @Column(nullable = false)
    @Positive
    var balance: BigDecimal = BigDecimal.ZERO.setScale(2),

    @Column(nullable = false)
    val annualInterestRate: BigDecimal = BigDecimal.ZERO.setScale(2),

    @Column(nullable = false)
    val currency: String = DEFAULT_CURRENCY,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: AccountType,

    @Column(nullable = false)
    val locked: Boolean = false,

    @Column(nullable = false)
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)

enum class AccountType { CHECKING, SAVINGS, LOAN }