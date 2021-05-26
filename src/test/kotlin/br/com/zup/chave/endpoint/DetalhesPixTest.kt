package br.com.zup.chave.endpoint

import br.com.zup.KeyManagerSearchGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.endpoint.databuild.ChavePixRequestBuilder
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.BankAccount
import br.com.zup.proxys.bcb.dto.Owner
import br.com.zup.proxys.bcb.dto.TypePersonProxy
import br.com.zup.proxys.bcb.dto.request.convert
import br.com.zup.proxys.bcb.dto.response.PixDetailsResponse
import com.google.rpc.BadRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class DetalhesPixTest(private val grpcClient:KeyManagerSearchGrpc.KeyManagerSearchBlockingStub) {

    @field:Inject
    lateinit var chaveRepository: ChaveRepository

    @field:Inject
    lateinit var bcbClient: BcbClient

    lateinit var chave:Chave

    @BeforeEach
    fun setup(){
        this.chaveRepository.deleteAll()
        this.chave =  this.chaveRepository.save(chave())
    }

    @Factory
    private class Clients{
        @Singleton
        fun searchGrpcBlockinStub(@GrpcChannel(GrpcServerChannel.NAME) channel:ManagedChannel)
        :KeyManagerSearchGrpc.KeyManagerSearchBlockingStub?{
            return KeyManagerSearchGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(BcbClient::class)
    fun bcbClient():BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @Test
    fun `Deve retornar Pix Interno`(){

        Assertions.assertEquals(1,this.chaveRepository.count())
        this.retornarPixBcbMock()
        val response = this.grpcClient.consultarChave(ChavePixRequestBuilder.requestInternoPixAleatorioValido(chave))

        Assertions.assertNotNull(response)
        Assertions.assertEquals(1,this.chaveRepository.count())
    }

    @Test
    fun `Deve retornar Pix Externo`(){
        this.retornarPixBcbMock()
        val response = this.grpcClient.consultarChave(ChavePixRequestBuilder.requestExternoPixAleatorioValido())

        Assertions.assertNotNull(response)
        Assertions.assertEquals(1,this.chaveRepository.count())
    }


    @Test
    fun `Nao deve retornar Pix nao cadastrado no banco central Interno`(){

        this.retornarNotFoundPixBcbMock()

        val throws = assertThrows<StatusRuntimeException> {
            this.grpcClient.consultarChave(ChavePixRequestBuilder.requestInternoPixAleatorioValido(chave))
        }

        with(throws){
            Assertions.assertEquals(Status.NOT_FOUND.code,status.code)
            Assertions.assertEquals("NOT_FOUND: A chave inserida nao esta cadastrada como uma chave PIX",
                throws.localizedMessage)
        }
    }

    @Test
    fun `Nao deve retornar Pix nao cadastrado no banco central Externo`(){

        this.retornarNotFoundPixBcbMock()

        val throws = assertThrows<StatusRuntimeException> {
            this.grpcClient.consultarChave(ChavePixRequestBuilder.requestExternoPixAleatorioValido())
        }

        with(throws){
            Assertions.assertEquals(Status.NOT_FOUND.code,status.code)
            Assertions.assertEquals("NOT_FOUND: A chave inserida nao esta cadastrada como uma chave PIX",
                throws.localizedMessage)
        }
    }


    @Test
    fun `Nao deve receber Pix vazio ou nulo Externo`(){
        this.retornarNotFoundPixBcbMock()

        val throws = assertThrows<StatusRuntimeException> {
            this.grpcClient.consultarChave(ChavePixRequestBuilder.requestExternoPixAleatorioNulo())
        }

        with(throws){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            Assertions.assertEquals("INVALID_ARGUMENT: A chave inserida nao pode ser nula ou vazia.",
                throws.localizedMessage)
        }
    }

    @Test
    fun `Nao deve receber Pix vazio ou nulo Interno`(){
        this.retornarNotFoundPixBcbMock()

        val throws = assertThrows<StatusRuntimeException> {
            this.grpcClient.consultarChave(ChavePixRequestBuilder.requestInternoPixAleatorioNulo())
        }

        with(throws){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertThat(violations(),containsInAnyOrder(
                Pair("pixId", "Nao pode ser nula ou vazio"),
                Pair("clienteId", "Nao pode ser nula ou vazio")
            ))
        }
    }



    private fun retornarPixBcbMock(){
        Mockito.`when`(this.bcbClient.consultarDetalhesPix(any())).thenReturn(responseDetails())
    }

    private fun retornarNotFoundPixBcbMock(){
        Mockito.`when`(this.bcbClient.consultarDetalhesPix(any())).thenReturn(null)
    }

    private fun responseDetails():PixDetailsResponse
    {
        val chave = this.chave()
        return PixDetailsResponse(
            chave.tipoChave.convert(),
            chave.keyPix,
            BankAccount(chave.client.instituicao.ispb,
                chave.client.conta.agencia,
                chave.client.conta.numero,
                chave.tipoConta.convert()),
            Owner(TypePersonProxy.NATURAL_PERSON,
                chave.client.nome,
                chave.client.cpf),
            LocalDateTime.now()
        )
    }


    private fun chave():Chave{
        return Chave(Client(ChavePixRequestBuilder.clientIdDefault(), "Cliente","180.699.330-97",
            Instituicao("ITAU","939249"),
            Conta("123","213123")),
            ChavePixRequestBuilder.pixDefault(),TipoChave.ALEATORIO,TipoConta.CONTA_CORRENTE)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)
    private fun <T> any(): T = Mockito.any()

    private fun StatusRuntimeException.violations(): List<Pair<String, String>>? {
        val details = StatusProto.fromThrowable(this)
            ?.detailsList?.get(0)!!
            .unpack(BadRequest::class.java)

        return details.fieldViolationsList
            .map { it.field to it.description }
    }

}