package com.bank.toybank.transaction.policies

import com.bank.toybank.account.persistence.AccountEntity
import com.bank.toybank.account.persistence.AccountType
import com.bank.toybank.common.CASH
import com.bank.toybank.transaction.persistence.TransactionEntity
import org.iban4j.Iban
import java.math.BigDecimal
import java.util.*

interface Policy {
    fun apply(tx: TransactionEntity): PolicyApplicationResult
}

class NegativeAmountPolicy : Policy {

    override fun apply(tx: TransactionEntity) =
        if (tx.amount <= BigDecimal.ZERO) {
            PolicyApplicationResult(
                satisfied = false,
                cause = "Transactions with negative or 0 amount value are not supported."
            )
        } else PolicyApplicationResult()
}

class AccountInOurBankPolicy(private val belongsToOurBank: (String?) -> Boolean) : Policy {

    override fun apply(tx: TransactionEntity): PolicyApplicationResult {
        val accountsOfOurBank = listOf(tx.fromAccount, tx.toAccount)
            .filter { it != CASH }
            .filter { iban -> belongsToOurBank(iban) }
            .count()

        return if (accountsOfOurBank > 0)
            PolicyApplicationResult()
        else
            PolicyApplicationResult(
                satisfied = false,
                cause = "Transactions should involve at least one account of our bank."
            )
    }
}

class AccountExistsPolicy(private val belongsToOurBank: (String?) -> Boolean,
                          private val findAccount: (Iban) -> Optional<AccountEntity>) : Policy {

    override fun apply(tx: TransactionEntity): PolicyApplicationResult {
        val existingAccounts = listOf(tx.fromAccount, tx.toAccount)
            .filter { it != CASH }
            .filter { iban -> belongsToOurBank(iban) }
            .filter { iban -> findAccount(Iban.valueOf(iban)).isPresent }
            .count()

        return if (existingAccounts > 0)
            PolicyApplicationResult()
        else
            PolicyApplicationResult(satisfied = false, cause = "Transactions should involve at least one account of our bank.")
    }
}

class SameAccountPolicy : Policy {
    override fun apply(tx: TransactionEntity) =
        if (tx.fromAccount == tx.toAccount)
            PolicyApplicationResult(satisfied = false, cause = "Transfer to the same account is not allowed.")
        else
            PolicyApplicationResult()
}

class OverdraftPolicy(private val belongsToOurBank: (String?) -> Boolean,
                      private val findAccount: (Iban) -> Optional<AccountEntity>) : Policy {

    override fun apply(tx: TransactionEntity) = listOf(
        checkOverdraft(tx.fromAccount, tx.amount),
        checkOverdraft(tx.toAccount, tx.amount.negate())
    ).firstOrNull { it.satisfied.not() } ?: PolicyApplicationResult()

    private fun checkOverdraft(iban: String?, amount: BigDecimal): PolicyApplicationResult {
        if (iban != CASH && belongsToOurBank(iban)) {
            val expectedBalance = findAccount(Iban.valueOf(iban)).orElseThrow().balance - amount
            if (expectedBalance < BigDecimal.ZERO) {
                return PolicyApplicationResult(
                    satisfied = false,
                    cause = "Insufficient funds, overdraft is not allowed."
                )
            }
        }
        return PolicyApplicationResult()
    }
}

class DepositPolicy : Policy {
    /* currently deposit is allowed to any bank account */
    override fun apply(tx: TransactionEntity): PolicyApplicationResult = PolicyApplicationResult()
}

class WithdrawalPolicy(private val belongsToOurBank: (String) -> Boolean,
                       private val findAccount: (Iban) -> Optional<AccountEntity>) : Policy {

    override fun apply(tx: TransactionEntity): PolicyApplicationResult {
        if (tx.fromAccount != CASH) {
            val withdrawalAccount = findAccount(Iban.valueOf(tx.fromAccount))
            if (withdrawalAccount.isPresent && belongsToOurBank(withdrawalAccount.get().iban)) {
                when (withdrawalAccount.get().type) {
                    AccountType.SAVINGS -> if (tx.toAccount != withdrawalAccount.get().referenceAccount?.iban) {
                        return PolicyApplicationResult(
                            satisfied = false,
                            cause = "Withdrawal from savings account allowed only from reference checking account."
                        )
                    }
                    AccountType.LOAN -> return PolicyApplicationResult(
                        satisfied = false,
                        cause = "Withdrawal from personal loan account is not allowed."
                    )
                    AccountType.CHECKING -> { /* allowed */
                    }
                }
            }
        }
        return PolicyApplicationResult()
    }
}