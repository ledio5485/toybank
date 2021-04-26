package com.bank.toybank.account

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class AccountNumberGenerator {
    private val counter = AtomicInteger()

    fun generate() = counter.getAndIncrement()
}