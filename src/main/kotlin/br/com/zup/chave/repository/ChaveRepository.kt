package br.com.zup.chave.repository

import br.com.zup.chave.domain.Chave
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository : JpaRepository<Chave,Long> {

    fun existsByKeyPix(chave:String):Boolean

    fun findByKeyPix(chave:String):Optional<Chave>

    fun findByUuid(uuid:String):Optional<Chave>

}