package br.com.zup.chave.domain

import br.com.zup.chave.repository.ClientRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
class ClientDomainTest(  private val repository: ClientRepository) {



    @Test
    fun `Deve persistir objeto de dominio de cliente`(){

        this.repository.deleteAll()

        val client  = this.repository.save(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123"))
        )
        Assertions.assertEquals(1,repository.count())
        Assertions.assertNotNull(client.conta)
        Assertions.assertNotNull(client.nome)
        Assertions.assertNotNull(client.cpf)
        Assertions.assertNotNull(client.instituicao)
        Assertions.assertNotNull(client.conta)
        Assertions.assertNotNull(client.conta.agencia)
        Assertions.assertNotNull(client.conta.numero)
        Assertions.assertNotNull(client.id)
        Assertions.assertNotNull(client.uuid)
        Assertions.assertNotNull(client.instituicao.ispb)
        Assertions.assertNotNull(client.instituicao.nomeInstituicao)

    }
}