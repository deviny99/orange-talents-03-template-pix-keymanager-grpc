package br.com.zup.chave.endpoint

import br.com.zup.ChaveRequest
import br.com.zup.KeyManagerServiceGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.proxys.itau.ClienteProxyResponse
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Singleton

@MicronautTest(transactional = false)
class CadastroChaveTest(
    private val grpcClient : KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
    private val itauClient : ItauClient,
    private val repository: ChaveRepository) {

    private val idClienteValido = "2b62dad8-b8a5-11eb-8529-0242ac130003"
    private val idClienteInvalido = "1"
    private val cpfValido = "596.265.190-10"
    private val cpfInvalido = "abc.123.dfg-45"
    private val emailValido = "email@email.com"
    private val emailInvalido = "email"
    private val celularValido = "+55(19)99999-9999"
    private val celularInvalido = "(19)99999-9999"

    @Test
    fun `Deve cadastrar chave com cpf`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val respose = this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(cpfValido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }


    @Test
    fun `Deve cadastrar chave com email`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val respose = this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
            .setIdClient(idClienteValido)
            .setChave(emailValido)
            .setTipo(TipoChave.EMAIL)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Deve cadastrar chave aleatoria`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val respose = this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
            .setIdClient(idClienteValido)
            .setChave("")
            .setTipo(TipoChave.ALEATORIO)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Deve cadastar com chave nula ou vazia portanto que seja do tipo aleatorio`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val response =   this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
            .setIdClient(idClienteValido)
            .setTipo(TipoChave.ALEATORIO)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        Assertions.assertNotNull(response.id)

    }

    @Test
    fun `Deve cadastrar chave com celular`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val respose = this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
            .setIdClient(idClienteValido)
            .setChave(celularValido)
            .setTipo(TipoChave.CELULAR)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build())

        Assertions.assertNotNull(respose.id)
        Assertions.assertEquals(1, repository.count())

    }

    @Test
    fun `Nao deve cadastrar chave ja existente`(){

        this.repository.save(Chave(Client(idClienteValido, "Cliente",cpfValido,
            Instituicao("ITAU","939249"),
            Conta("123","213123")),
            celularValido,TipoChave.CELULAR,TipoConta.CONTA_CORRENTE))

       this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(celularValido)
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.ALREADY_EXISTS.code,exception.status.code)
        }

    }


    @Test
    fun `Nao deve cadastrar chave do tipo cpf do formato invalido`(){

        this.repository.save(Chave(Client(idClienteValido, "Cliente",cpfValido,
            Instituicao("ITAU","939249"),
            Conta("123","213123")),
            celularValido,TipoChave.CPF,TipoConta.CONTA_CORRENTE))

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(cpfInvalido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }

    }

    @Test
    fun `Nao deve cadastrar chave com mais de 77 caracteres`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(this.chaveLimiteCaracteres())
                .setTipo(TipoChave.EMAIL)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave vazia`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave("")
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }

    }

    @Test
    fun `Nao deve cadastrar chave de tipos errados`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient("1")
                .setChave(celularValido)
                .setTipo(TipoChave.EMAIL)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave que o identificador nao pertence a uma conta`(){

        this.repository.deleteAll()

        this.mockarConsultaClientNotFound()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(cpfValido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave com o id do cliente nulo ou vazio`(){
        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient("")
                .setChave(cpfValido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave caso o cliente nao possua aquele tipo de conta vinculada ao banco`(){
        this.repository.deleteAll()

        this.mockarConsultaClientNotFound()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteValido)
                .setChave(cpfValido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_POUPANCA)
                .build())
        }

        with(exception)
        {
            println(exception.message)
            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave com o id do cliente do formato invalido`(){

        this.repository.deleteAll()

        this.mockarConsultaCliente()

        val exception = assertThrows<StatusRuntimeException> {
            this.grpcClient.cadastrarChave(ChaveRequest.newBuilder()
                .setIdClient(idClienteInvalido)
                .setChave(cpfValido)
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build())
        }

        with(exception)
        {
            println(exception.message)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }




    @Factory
    class Clients{

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):KeyManagerServiceGrpc.KeyManagerServiceBlockingStub?{
            return KeyManagerServiceGrpc.newBlockingStub(channel)
        }

    }

    private fun mockarConsultaCliente(){
        Mockito.`when`(itauClient.consultarClient(idClienteValido,TipoConta.CONTA_CORRENTE)).thenReturn(
            ClienteProxyResponse(titular = ClienteProxyResponse.TitularProxyDto(idClienteValido,
                nome="fulano",
                cpf=cpfValido,),
                tipo = TipoConta.CONTA_CORRENTE,
                agencia = "0001",
                numero = "12345678",
                instituicao = ClienteProxyResponse.InstituicaoProxyDTO(nome = "ITAU", ispb = "2182139"))
        )
    }

    private fun mockarConsultaClientNotFound(){
        Mockito.`when`(itauClient.consultarClient(idClienteInvalido,TipoConta.CONTA_POUPANCA)).thenReturn(null)
    }

    @MockBean(ItauClient::class)
    fun itauClient(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

    private fun chaveLimiteCaracteres():String{
        var sb = StringBuffer()
        for (i:Int in 0..77){
            sb.append("1")
        }
        return sb.toString()
    }

}