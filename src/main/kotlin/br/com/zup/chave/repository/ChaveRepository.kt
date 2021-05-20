package br.com.zup.chave.repository

import br.com.zup.chave.domain.Chave
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChaveRepository : JpaRepository<Chave,Long> {

    fun existsByKeyPix(chave:String):Boolean

}