package br.com.zup.enpoint

import br.com.zup.KeyManagerSearchGrpc
import br.com.zup.PixDetalhes
import br.com.zup.PixRequestExterno
import br.com.zup.PixRequestInterno
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.interceptor.handler.ErrorHandlerSearch
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.proxys.bcb.BcbClient
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandlerSearch
class KeyManagerSearchService(@field:Inject val chaveRepository: ChaveRepository,
                              @field:Inject val bcbClient: BcbClient)
    : KeyManagerSearchGrpc.KeyManagerSearchImplBase() {

    override fun buscaExterna(request: PixRequestExterno, responseObserver: StreamObserver<PixDetalhes>?) {

        request.notNulls()

        val retorno = this.bcbClient.consultarDetalhesPix(request.pix)
            ?: throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")

        responseObserver?.onNext(retorno.toPixDetalhes())
        responseObserver?.onCompleted()

    }

    override fun buscaInterna(request: PixRequestInterno, responseObserver: StreamObserver<PixDetalhes>?) {

        request.notNulls()

        val chave = this.chaveRepository.findByUuid(request.pixId).orElseThrow {
            throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")
        }

        if (chave.client.uuid != request.clienteId){
            throw GrpcExceptionRuntime.notFound("A chave inserida nao pertence ao cliente informado")
        }

        val retorno = this.bcbClient.consultarDetalhesPix(chave.keyPix)
            ?: throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")

        responseObserver?.onNext(retorno.toPixDetalhes(chave.uuid,chave.client.uuid))
        responseObserver?.onCompleted()
    }
}