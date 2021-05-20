package br.com.zup.chave.domain

import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.repository.ChaveRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*

@MicronautTest
class ChaveDomainTest(private val chaveRepository: ChaveRepository) {


    @Test
    fun `Deve inserir chave para tipo aleatorio`(){

        try {
            val chaveDomain = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
                Instituicao("ITAU","234234"), Conta("12312","123123")),
                null,
                TipoChave.ALEATORIO,
                TipoConta.CONTA_CORRENTE
            )

            Assertions.assertNotNull(chaveDomain.keyPix)

        }catch (ex:Exception){

            fail(ex.message)

        }
    }

    @Test
    fun `Deve inserir chave para tipo nao aleatorio`(){
        try {
            val email : String = "email@email.com"
            val chaveDomain = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
                Instituicao("ITAU","234234"), Conta("12312","123123")),
                email,
                TipoChave.EMAIL,
                TipoConta.CONTA_CORRENTE
            )

            Assertions.assertNotNull(chaveDomain.keyPix)
            Assertions.assertEquals(email,chaveDomain.keyPix)

        }catch (ex:Exception){

            fail(ex.message)

        }
    }

    @Test
    fun `Nao deve receber chave nula caso nao seja uma chave do tipo aleatoria`(){
        try {
            Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
                Instituicao("ITAU","234234"), Conta("12312","123123")),
                "",
                TipoChave.EMAIL,
                TipoConta.CONTA_CORRENTE
            )
            fail("")
        }catch (ex:Exception){
            Assertions.assertNotNull(ex)
        }
    }

    @Test
    fun `Deve persistir objeto de dominio de chave com chave aleatoria`(){

        this.chaveRepository.deleteAll()

        val chavePrePersiste = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        Assertions.assertEquals(0,chaveRepository.count())
        Assertions.assertNotNull(chavePrePersiste.client)
        Assertions.assertNull(chavePrePersiste.id)
        Assertions.assertNotNull(chavePrePersiste.keyPix)
        Assertions.assertNotEquals(chavePrePersiste.keyPix,chavePrePersiste.keyValue)
        Assertions.assertNotNull(chavePrePersiste.tipoChave)
        Assertions.assertNotNull(chavePrePersiste.uuid)
        Assertions.assertNotNull(chavePrePersiste.tipoConta)

        val chavePostPersist  = this.chaveRepository.save(chavePrePersiste )
        Assertions.assertEquals(1,chaveRepository.count())
        Assertions.assertNotNull(chavePostPersist.client)
        Assertions.assertNotNull(chavePostPersist.id)
        Assertions.assertNotNull(chavePostPersist.keyPix)
        Assertions.assertNotNull(chavePostPersist.tipoChave)
        Assertions.assertNotNull(chavePostPersist.uuid)
        Assertions.assertNotNull(chavePostPersist.tipoConta)


    }

}