package br.com.zup.chave.endpoint

import br.com.zup.ChaveRequest
import br.com.zup.KeyManagerRegistryGrpc
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.endpoint.databuild.ChaveRequestBuilder
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.request.CreatePixKeyRequestProxy
import br.com.zup.proxys.bcb.dto.response.CreatePixKeyResponse
import br.com.zup.proxys.itau.ClienteProxyResponse
import br.com.zup.proxys.itau.ItauClient
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import java.time.LocalDate
import java.time.LocalDateTime

@MicronautTest(transactional = false)
internal class CadastroChaveTest(
    private val grpcClient : KeyManagerRegistryGrpc.KeyManagerRegistryBlockingStub) {

    private val idClienteValido = "2b62dad8-b8a5-11eb-8529-0242ac130003"
    private val cpfValido = "596.265.190-10"


    @field:Inject
    lateinit var  itauClient : ItauClient
    @field:Inject
    lateinit var bcbClient: BcbClient

    @field:Inject
    lateinit var repository: ChaveRepository

    @MockBean(BcbClient::class)
    fun bcbClient():BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient? {
        return Mockito.mock(ItauClient::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                KeyManagerRegistryGrpc.KeyManagerRegistryBlockingStub?{
            return KeyManagerRegistryGrpc.newBlockingStub(channel)
        }
    }

    @BeforeEach
    fun setup(){
        this.repository.deleteAll()
    }

    @Test
    fun `Deve cadastrar chave com cpf`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()
        val respose = this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestCpfValido())
        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Deve cadastrar chave com email`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()
        val respose = this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestEmailValido())
        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }


    @Test
    fun `Deve cadastrar chave aleatoria`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val respose = this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveAleatorio())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Deve cadastrar chave com celular`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()
        val respose = this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestCelularValido())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Deve cadastrar chave do tipo aleatorio nao nulo com valor ja existente`(){

        val request = ChaveRequestBuilder.requestTipoAleatorioNaoNuloJaExistente()

        this.repository.save(Chave(Client(idClienteValido, "Cliente",cpfValido,
            Instituicao("ITAU","939249"),
            Conta("123","213123")),
            request.chave,request.tipo,request.tipoConta))

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val respose = this.grpcClient.cadastrarChave(request)

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(2, repository.count())

    }

    @Test
    fun `Nao deve cadastrar chave ja existente`(){

        val request = ChaveRequestBuilder.requestCelularValido()

        this.repository.save(Chave(Client(idClienteValido, "Cliente",cpfValido,
            Instituicao("ITAU","939249"),
            Conta("123","213123")),
            request.chave,request.tipo,request.tipoConta))

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(request)
        }

        with(exception)
        {
            Assertions.assertEquals(Status.ALREADY_EXISTS.code,exception.status.code)

        }

    }


    @Test
    fun `Nao deve cadastrar chave do tipo cpf do formato invalido`(){

        val request = ChaveRequestBuilder.requestCpfInvalido()

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(request)
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
            assertThat(violations(), containsInAnyOrder(
                Pair("cpf", "a chave cpf esta no formato invalido.")
            ))
        }

    }

    @Test
    fun `Nao deve cadastrar chave do tipo email do formato invalido`(){

        val request = ChaveRequestBuilder.requestEmailInvalido()


        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(request)
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
            assertThat(violations(), containsInAnyOrder(
                Pair("email", "a chave do tipo email esta no formato invalido.")
            ))
        }

    }

    @Test
    fun `Nao deve cadastrar chave com mais de 77 caracteres`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveComTamanhoInvalido())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave vazia`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveVaziaDoTipoNaoAleatorio())
        }

        with(exception)
        {

            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)

        }

    }

    @Test
    fun `Nao deve cadastrar chave de tipos errados`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveDeTipoInvalido())
        }

        with(exception)
        {

            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
            assertThat(violations(), containsInAnyOrder(
                Pair("celular", "a chave celular esta no formato invalido.")
            ))
        }
    }

    @Test
    fun `Nao deve cadastrar chave que o identificador nao pertence a uma conta`(){

        this.consultaERPComNotFoundMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveAleatorio())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
            Assertions.assertEquals("NOT_FOUND: Cliente ou Conta invalido",exception.localizedMessage)
        }
    }

    @Test
    fun `Nao deve cadastrar chave com o id do cliente nulo ou vazio`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestIdClientNulo())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
            Assertions.assertEquals("INVALID_ARGUMENT: O id do cliente não pode ser nulo",exception.localizedMessage)
        }
    }

    @Test
    fun `Nao deve cadastrar chave caso o cliente nao possua aquele tipo de conta vinculada ao banco`(){

        this.consultaERPComNotFoundMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestChaveAleatorio())
        }

        with(exception)
        {

            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
            Assertions.assertEquals("NOT_FOUND: Cliente ou Conta invalido",exception.localizedMessage)
        }
    }


    @Test
    fun `Nao deve cadastrar chave com o id do cliente do formato invalido`(){

        this.consultaERPComSucessoMock()
        this.registrarPixBcbMock()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequestBuilder.requestIdClientFormatoInvalido())
        }

        with(exception)
        {
            println(exception.message)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }



    private fun cliente(chaveRequest: ChaveRequest):Client{
        return Client(chaveRequest.idClient,"fulano",cpfValido,
            Instituicao("ITAU","123"),Conta("0001","212233"))
    }

    private fun clienteResponse():ClienteProxyResponse{
        return ClienteProxyResponse(titular = ClienteProxyResponse.TitularProxyDto(idClienteValido,
            nome="fulano",
            cpf=cpfValido),
            tipo = TipoConta.CONTA_CORRENTE,
            agencia = "0001",
            numero = "12345678",
            instituicao = ClienteProxyResponse.InstituicaoProxyDTO(nome = "ITAU", ispb = "2182139"))
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


    private fun consultaERPComNotFoundMock(){
        Mockito.`when`(itauClient.consultarClient(any(),any())).thenReturn(null)
    }

    private fun consultaERPComSucessoMock(){
        Mockito.`when`(itauClient.consultarClient(any(),any())).thenReturn(clienteResponse())
    }

    private fun registrarPixBcbMock(){
        Mockito.`when`(bcbClient.cadastrarChavePix(any(CreatePixKeyRequestProxy::class.java)))
            .thenReturn(responsePix())
    }

    private fun naoRegistrarPixBcbMock(){
        Mockito.`when`(bcbClient.cadastrarChavePix(any(CreatePixKeyRequestProxy::class.java)))
            .thenReturn(null)
    }


    fun responsePix(): CreatePixKeyResponse {
        return CreatePixKeyResponse("823482",LocalDateTime.now())
    }
}