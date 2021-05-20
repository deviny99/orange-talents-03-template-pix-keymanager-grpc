package br.com.zup.chave.repository

import br.com.zup.chave.domain.Client
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ClientRepository : JpaRepository<Client,Long>{

    fun findByUuid(uuid: String):Optional<Client>


}