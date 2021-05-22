package br.com.zup.enpoint

import br.com.zup.ChaveRemovidaResponse
import br.com.zup.ClienteChaveRequest
import br.com.zup.KeyManagerDeleteGrpc
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.interceptor.handler.ErrorHandlerDelete
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.enpoint.extensions.retornarChave
import br.com.zup.enpoint.extensions.verificaDonoChave
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.request.DeleteChavePixRequestProxy
import io.grpc.stub.StreamObserver
import io.micronaut.transaction.SynchronousTransactionManager
import java.sql.Connection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandlerDelete
open class KeyManagerDeleteService(@field:Inject private val chaveRepository: ChaveRepository,
                                   @field:Inject private val bcbClient: BcbClient,
                                   @field:Inject private val transactionalSync:SynchronousTransactionManager<Connection>)
    : KeyManagerDeleteGrpc.KeyManagerDeleteImplBase(){


    override fun removerChave(request: ClienteChaveRequest, responseObserver: StreamObserver<ChaveRemovidaResponse>?) {

        this.transactionalSync.executeWrite {

            request.notNulls()
            val pix = request.retornarChave(this.chaveRepository)
            request.verificaDonoChave(pix)

            this.chaveRepository.deleteById(pix.id!!)

           bcbClient.deletarChavePix(pix.keyPix, DeleteChavePixRequestProxy(pix.keyPix,pix.client.instituicao.ispb))
                ?:throw GrpcExceptionRuntime.notFound("Não possivel remover a chave informada, pois não está cadastrada")

        }

        responseObserver?.onNext(ChaveRemovidaResponse
            .newBuilder()
            .setMessage("Chave removida com sucesso")
            .build())
        responseObserver?.onCompleted()

    }

}