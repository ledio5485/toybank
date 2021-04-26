package com.bank.toybank.transaction.policies

import com.bank.toybank.common.CASH
import com.bank.toybank.transaction.persistence.TransactionEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PoliciesTest {

    @Test
    internal fun `should not validate a transaction with negative amount`() {
        val tx = TransactionEntity(fromAccount = "A", toAccount = "B", amount = BigDecimal.ONE.negate())

        val actual = NegativeAmountPolicy().apply(tx)

        val expected = PolicyApplicationResult(
            satisfied = false,
            cause = "Transactions with negative or 0 amount value are not supported."
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should not validate a transaction with amount = 0`() {
        val tx = TransactionEntity(fromAccount = "A", toAccount = "B", amount = BigDecimal.ZERO)

        val actual = NegativeAmountPolicy().apply(tx)

        val expected = PolicyApplicationResult(
            satisfied = false,
            cause = "Transactions with negative or 0 amount value are not supported."
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should validate a transaction with positive amount`() {
        val tx = TransactionEntity(fromAccount = "A", toAccount = "B", amount = BigDecimal.ONE)

        val actual = NegativeAmountPolicy().apply(tx)

        val expected = PolicyApplicationResult()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should not validate a transaction when both accounts does not belong to the bank`() {
        val tx = TransactionEntity(fromAccount = "external-From", toAccount = "external-To", amount = BigDecimal.ONE)
        val belongsToOurBank: (String?) -> Boolean = { iban -> iban != null && iban.contains("external").not() }

        val actual = AccountInOurBankPolicy(belongsToOurBank).apply(tx)

        val expected = PolicyApplicationResult(
            satisfied = false,
            cause = "Transactions should involve at least one account of our bank."
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should validate a transaction when at least one account belongs to the bank`() {
        val tx = TransactionEntity(fromAccount = "external-From", toAccount = "To", amount = BigDecimal.ONE)
        val belongsToOurBank: (String?) -> Boolean = { iban -> iban != null && iban.contains("external").not() }

        val actual = AccountInOurBankPolicy(belongsToOurBank).apply(tx)

        val expected = PolicyApplicationResult()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should not validate a transaction with same sender and receiver`() {
        val tx = TransactionEntity(fromAccount = "A", toAccount = "A", amount = BigDecimal.ONE)

        val actual = SameAccountPolicy().apply(tx)

        val expected = PolicyApplicationResult(satisfied = false, cause = "Transfer to the same account is not allowed.")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    internal fun `should validate always a deposit transaction`() {
        val tx = TransactionEntity(fromAccount = CASH, toAccount = "A", amount = BigDecimal.ONE)

        val actual = DepositPolicy().apply(tx)

        val expected = PolicyApplicationResult()
        assertThat(actual).isEqualTo(expected)
    }

    // TODO("add validation for these policies: [AccountExistsPolicy, OverdraftPolicy, WithdrawalPolicy]")
}