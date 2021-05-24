package br.com.zup.chave.endpoint

import br.com.zup.ClienteRequest
import br.com.zup.KeyManagerListKeysGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.endpoint.databuild.ChavePixRequestBuilder
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import br.com.zup.proxys.itau.DadosClienteProxyResponse
import br.com.zup.proxys.itau.ItauClient
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
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class ListagemPixTest(private val grpcClient:KeyManagerListKeysGrpc.KeyManagerListKeysBlockingStub) {

    @field:Inject
    lateinit var chaveRepository: ChaveRepository

    @field:Inject
    lateinit var clientRepository: ClientRepository

    @field:Inject
    lateinit var itauClient: ItauClient

    @Factory
    private class Clients{
        @Singleton
        fun listGrpcBlockinStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerListKeysGrpc.KeyManagerListKeysBlockingStub?{
            return KeyManagerListKeysGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ItauClient::class)
    fun itauClient():ItauClient?{
        return Mockito.mock(ItauClient::class.java)
    }

    @BeforeEach
    fun setup(){
        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()

        this.chaveRepository.save(chave(client()))
        this.chaveRepository.save(chave(client()))
        this.chaveRepository.save(chave(client()))
        this.chaveRepository.save(chave(client()))
        this.chaveRepository.save(chave(client()))
        this.chaveRepository.save(chave(client()))
    }

    @Test
    fun `Deve retornar lista com chaves`(){

        this.idClientValidoMock()

        val response = grpcClient.listagemChaves(ClienteRequest
            .newBuilder()
            .setClienteId(client().uuid)
            .build())

        println(response.chavesList)
        Assertions.assertFalse(response.chavesList.isEmpty())
    }

    @Test
    fun `Deve retornar lista vazia`(){

        this.idClientValidoMock()
        this.chaveRepository.deleteAll()

        val response = grpcClient.listagemChaves(ClienteRequest
            .newBuilder()
            .setClienteId(UUID.randomUUID()
                .toString())
            .build())

        Assertions.assertTrue(response.chavesList.isEmpty())
    }
    @Test
    fun `Nao deve retornar lista com id de cliente invalido`(){

        this.idClientInvalidoMock()

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.listagemChaves(ClienteRequest
            .newBuilder()
            .setClienteId(UUID.randomUUID()
                .toString())
            .build())
        }

        with(response){
            Assertions.assertNotNull(response)
            Assertions.assertEquals(Status.NOT_FOUND.code,response.status.code)
            Assertions.assertEquals("NOT_FOUND: O id informado não pertence a um cliente Itau",response.localizedMessage)
        }
    }

    private fun clienteResponse(): DadosClienteProxyResponse {
        return DadosClienteProxyResponse(id= client().uuid ,nome="fulano",
            cpf="296.492.980-70",
            instituicao = DadosClienteProxyResponse.InstituicaoProxyDTO(nome = "ITAU", ispb = "2182139"))
    }

    private fun idClientValidoMock(){
        Mockito.`when`(this.itauClient.consultarClient(any()))
            .thenReturn(clienteResponse())
    }

    private fun idClientInvalidoMock(){
        Mockito.`when`(this.itauClient.consultarClient(any()))
            .thenReturn(null)
    }

    private fun chave(cliente:Client):Chave{
        return Chave(cliente,
            UUID.randomUUID().toString(), TipoChave.ALEATORIO,TipoConta.CONTA_CORRENTE)
    }

    private fun client():Client{
        return Client(ChavePixRequestBuilder.clientIdDefault(), "Cliente","180.699.330-97",
            Instituicao("ITAU","939249"),
            Conta("123","213123"))
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)
    private fun <T> any(): T = Mockito.any()

}