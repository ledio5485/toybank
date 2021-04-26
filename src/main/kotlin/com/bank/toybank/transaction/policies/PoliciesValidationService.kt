package com.bank.toybank.transaction.policies

import com.bank.toybank.account.persistence.AccountEntity
import com.bank.toybank.account.persistence.AccountRepository
import com.bank.toybank.common.IbanService
import com.bank.toybank.transaction.persistence.TransactionEntity
import mu.KotlinLogging
import org.iban4j.Iban
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

data class PolicyApplicationResult(val satisfied: Boolean = true, val cause: String = "")

@Service
class PoliciesValidationService(
    private val accountRepository: AccountRepository,
    private val ibanService: IbanService
) {
    private val belongsToOurBank: (String?) -> Boolean = { iban -> ibanService.belongsToOurBank(iban) }
    private val findAccount: (Iban) -> Optional<AccountEntity> =
        { iban -> accountRepository.findByIban(iban.toString()) }

    private val policies = listOf(
        NegativeAmountPolicy(),
        AccountInOurBankPolicy(belongsToOurBank),
        AccountExistsPolicy(belongsToOurBank, findAccount),
        SameAccountPolicy(),
        OverdraftPolicy(belongsToOurBank, findAccount),
        DepositPolicy(),
        WithdrawalPolicy({ iban -> ibanService.belongsToOurBank(iban) }, findAccount)
    )

    fun apply(tx: TransactionEntity) {
        policies.map { it.apply(tx) }
            .filter { !it.satisfied }
            .also {
                require(it.isEmpty()) {
                    it.joinToString(prefix = "The transaction $tx didn't pass the validation. The violated policies: ") { par -> par.cause }
                        .also {
                            logger.error { it }
                            // TODO("The user has to be notified by email, sms or web app push notification.")
                        }
                }
            }
    }
}