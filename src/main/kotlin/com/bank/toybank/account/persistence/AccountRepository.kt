package com.bank.toybank.account.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository : JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {

    fun findByIban(iban: String): Optional<AccountEntity>

    fun findAllByTypeIsIn(accountType: List<AccountType>, pageable: Pageable): Page<AccountEntity>
}