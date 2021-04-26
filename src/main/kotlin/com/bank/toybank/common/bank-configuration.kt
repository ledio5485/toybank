package com.bank.toybank.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

const val DEFAULT_CURRENCY = "EUR"

@ConstructorBinding
@ConfigurationProperties(prefix = "toybank")
data class BankConfigurationProperties(
    val countryCode: String = "DE",
    val bankCode: String,
    val accountNumberLength: Int = 10,
    val currency: String = DEFAULT_CURRENCY
)