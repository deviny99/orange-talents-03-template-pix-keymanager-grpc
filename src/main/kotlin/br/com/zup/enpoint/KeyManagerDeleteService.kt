package br.com.zup.enpoint

import br.com.zup.ChaveRemovidaResponse
import br.com.zup.ClienteChaveRequest
import br.com.zup.KeyManagerDeleteGrpc
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.config.interceptor.handler.ErrorHandler
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.enpoint.extensions.retornarChave
import br.com.zup.enpoint.extensions.verificaDonoChave
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.request.DeleteChavePixRequestProxy
import io.grpc.stub.StreamObserver
import io.micronaut.transaction.SynchronousTransactionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
open class KeyManagerDeleteService(@field:Inject private val chaveRepository: ChaveRepository,
                                   @field:Inject private val bcbClient: BcbClient,
                                   @field:Inject private val transactionalSync:SynchronousTransactionManager<Connection>)
    : KeyManagerDeleteGrpc.KeyManagerDeleteImplBase(){

    private val logger : Logger = LoggerFactory.getLogger(KeyManagerDeleteService::class.java)

    override fun removerChave(request: ClienteChaveRequest, responseObserver: StreamObserver<ChaveRemovidaResponse>?) {


        this.transactionalSync.executeWrite {
            logger.info("Aplicando validações...")
            request.notNulls()
            val pix = request.retornarChave(this.chaveRepository)
            request.verificaDonoChave(pix)

            logger.info("Deletando chave no banco de dados...")
            this.chaveRepository.deleteById(pix.id!!)

            logger.info("Deletando chave no sistema do banco central...")
            bcbClient.deletarChavePix(pix.keyPix, DeleteChavePixRequestProxy(pix.keyPix,pix.client.instituicao.ispb))
                ?:throw GrpcExceptionRuntime.notFound("Não possivel remover a chave informada, pois não está cadastrada")
            logger.info("Chave removida com sucesso :)")
        }

        responseObserver?.onNext(ChaveRemovidaResponse
            .newBuilder()
            .setMessage("Chave removida com sucesso")
            .build())
        responseObserver?.onCompleted()

    }

}