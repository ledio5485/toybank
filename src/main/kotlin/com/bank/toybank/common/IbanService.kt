package com.bank.toybank.common

import com.bank.toybank.account.AccountNumberGenerator
import org.iban4j.CountryCode
import org.iban4j.Iban
import org.springframework.stereotype.Component

@Component
class IbanService(private val properties: BankConfigurationProperties, private val generator: AccountNumberGenerator) {
    fun belongsToOurBank(iban: Iban) =
        iban.countryCode.toString() == properties.countryCode && iban.bankCode == properties.bankCode

    fun belongsToOurBank(iban: String?) = if (iban.isNullOrBlank()) false else belongsToOurBank(Iban.valueOf(iban))

    fun generate(): Iban = Iban.Builder()
        .countryCode(CountryCode.getByCode(properties.countryCode))
        .bankCode(properties.bankCode)
        .accountNumber(String.format("%010d", generator.generate()).take(properties.accountNumberLength))
        .build()
}