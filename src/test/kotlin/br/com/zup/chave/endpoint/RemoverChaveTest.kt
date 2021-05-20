package br.com.zup.chave.endpoint

import br.com.zup.ClienteChaveRequest
import br.com.zup.KeyManagerServiceGrpc
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION)
class RemoverChaveTest(private val clientRepository: ClientRepository,
                       private val chaveRepository: ChaveRepository,
                       private val grpcClient : KeyManagerServiceGrpc.KeyManagerServiceBlockingStub) {


    @Factory
    class Clients{

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
        :KeyManagerServiceGrpc.KeyManagerServiceBlockingStub?{
            return KeyManagerServiceGrpc.newBlockingStub(channel)
        }

    }


    @Test
    fun `Deve remover chave`(){

        val chaveEntityPrePersist = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        this.chaveRepository.deleteAll()

        val chaveEntityPostPersist = this.chaveRepository.save(chaveEntityPrePersist)


        val  response = grpcClient.removerChave(ClienteChaveRequest
            .newBuilder()
            .setIdClient(chaveEntityPostPersist.client.uuid)
            .setChave(chaveEntityPostPersist.keyPix).build())

        val existsChave = this.chaveRepository.existsByKeyPix(chaveEntityPostPersist.keyPix)


        Assertions.assertNotNull(response)
        Assertions.assertEquals(0,this.chaveRepository.count())
        Assertions.assertFalse(existsChave)

    }

    @Test
    fun `Nao deve remover chave que nao esta cadastrada`(){

        val chaveEntityPrePersist = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()

        val exception = assertThrows<StatusRuntimeException> {
            val response = grpcClient.removerChave(ClienteChaveRequest
                .newBuilder()
                .setIdClient(chaveEntityPrePersist.client.uuid)
                .setChave(chaveEntityPrePersist.keyPix).build())
        }


        val existsChave = this.chaveRepository.existsByKeyPix(chaveEntityPrePersist.keyPix)

        with(exception){
            Assertions.assertFalse(existsChave)
            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
        }


    }

    @Test
    fun `Nao deve cadastrar chave que nao pertence ao cliente da solicitacao`(){

        val chaveEntityPrePersist = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()

        val chaveEntityPostPersist = this.chaveRepository.save(chaveEntityPrePersist)

        val exception = assertThrows<StatusRuntimeException> {
            val response = grpcClient.removerChave(ClienteChaveRequest
                .newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave(chaveEntityPostPersist.keyPix).build())
        }

        with(exception){

            Assertions.assertEquals(Status.NOT_FOUND.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave com dados id do cliente nulo ou vazio`(){
        val chaveEntityPrePersist = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()

        val exception = assertThrows<StatusRuntimeException> {
            val response = grpcClient.removerChave(ClienteChaveRequest
                .newBuilder()
                .setIdClient("")
                .setChave(chaveEntityPrePersist.keyPix).build())
        }

        val existsChave = this.chaveRepository.existsByKeyPix(chaveEntityPrePersist.keyPix)

        with(exception){
            Assertions.assertFalse(existsChave)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }

    @Test
    fun `Nao deve cadastrar chave com dados da chave nulo ou vazio`(){
        val chaveEntityPrePersist = Chave(Client(UUID.randomUUID().toString(),"Fulano","089.723.170-80",
            Instituicao("ITAU","234234"), Conta("12312","123123")),
            null,
            TipoChave.ALEATORIO,
            TipoConta.CONTA_CORRENTE
        )

        this.chaveRepository.deleteAll()
        this.clientRepository.deleteAll()

        val exception = assertThrows<StatusRuntimeException> {
            val response = grpcClient.removerChave(ClienteChaveRequest
                .newBuilder()
                .setIdClient(chaveEntityPrePersist.client.uuid)
                .setChave("")
                .build())
        }

      //  val existsChave = this.chaveRepository.existsByKeyPix(chaveEntityPrePersist.keyPix)

        with(exception){
            //Assertions.assertFalse(existsChave)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,exception.status.code)
        }
    }
}