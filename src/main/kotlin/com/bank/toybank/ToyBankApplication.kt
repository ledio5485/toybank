package com.bank.toybank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ToyBankApplication

fun main(args: Array<String>) {
	runApplication<ToyBankApplication>(*args)
}
