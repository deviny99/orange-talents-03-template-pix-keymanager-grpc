package br.com.zup.chave.endpoint

import br.com.zup.KeyManagerDeleteGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.endpoint.databuild.ClienteChaveRequestBuilder
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.request.DeleteChavePixRequestProxy
import br.com.zup.proxys.bcb.dto.response.DeletePixKeyResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class RemoverChaveTest(private val grpcClient : KeyManagerDeleteGrpc.KeyManagerDeleteBlockingStub) {


    @field:Inject
    lateinit var clientRepository: ClientRepository
    @field:Inject
    lateinit var  chaveRepository: ChaveRepository
    @field:Inject
    lateinit var bcbClient: BcbClient

    @Factory
    private class Client{

        @Singleton
        fun blockingStu(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        :KeyManagerDeleteGrpc.KeyManagerDeleteBlockingStub?{
            return KeyManagerDeleteGrpc.newBlockingStub(channel)
        }

    }


    @MockBean(BcbClient::class)
    fun bcbClient():BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @BeforeEach
    fun setup(){
        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()
    }


    @Test
    fun `Deve remover chave`(){

        this.deletarPixBcbMock()

        val chave = this.chaveRepository.save(chave())

        val  response = grpcClient.removerChave(ClienteChaveRequestBuilder
            .requestClientChaveValido(chave.client.uuid,chave.keyPix))

        val existsChave = this.chaveRepository.existsByKeyPix(chave.keyPix)

        Assertions.assertNotNull(response)
        Assertions.assertEquals(0,this.chaveRepository.count())
        Assertions.assertFalse(existsChave)

    }

    @Test
    fun `Nao deve remover chave que nao esta cadastrada`() {

        this.deletarPixBcbMock()

        val chave = this.chave()

        val exception = assertThrows<StatusRuntimeException> {
                grpcClient.removerChave(ClienteChaveRequestBuilder.requestClientChaveValido(chave.client.uuid, chave.keyPix))
        }

        val existsChave = this.chaveRepository.existsByKeyPix(chave = chave.keyPix)

        with(exception) {
            Assertions.assertFalse(existsChave)
            Assertions.assertEquals(Status.NOT_FOUND.code, exception.status.code)
        }
    }

    private fun deletarPixBcbMock(){
        Mockito.`when`(bcbClient.deletarChavePix(any(),any(DeleteChavePixRequestProxy::class.java)))
            .thenReturn(responsePixDelete())
    }

    private fun responsePixDelete(): DeletePixKeyResponse {
        return DeletePixKeyResponse(UUID.randomUUID().toString(),"123123", LocalDateTime.now())
    }

    private fun chave():Chave{
        return Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            UUID.randomUUID().toString(),
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)
    private fun <T> any(): T = Mockito.any()

}