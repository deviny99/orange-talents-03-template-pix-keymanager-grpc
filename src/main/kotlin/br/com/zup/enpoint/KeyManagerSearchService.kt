package br.com.zup.enpoint

import br.com.zup.*
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.config.interceptor.handler.ErrorHandler
import br.com.zup.enpoint.extensions.filtro
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.proxys.bcb.BcbClient
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class KeyManagerSearchService(@field:Inject val chaveRepository: ChaveRepository,
                              @field:Inject val bcbClient: BcbClient)
    : KeyManagerSearchGrpc.KeyManagerSearchImplBase() {

    private val logger : Logger = LoggerFactory.getLogger(KeyManagerSearchService::class.java)

    override fun consultarChave(request: PixRequest, responseObserver: StreamObserver<PixDetalhes>?) {

        request.notNulls()
        val retorno = request.filtro().filtrar(this.chaveRepository,this.bcbClient)?:
        throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")

        responseObserver?.onNext(retorno)
        responseObserver?.onCompleted()
    }

}