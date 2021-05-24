package br.com.zup.enpoint

import br.com.zup.KeyManagerSearchGrpc
import br.com.zup.PixDetalhes
import br.com.zup.PixRequestExterno
import br.com.zup.PixRequestInterno
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.config.interceptor.handler.ErrorHandler
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

    override fun buscaExterna(request: PixRequestExterno, responseObserver: StreamObserver<PixDetalhes>?) {

        logger.info("Aplicando validações...")
        request.notNulls()

        logger.info("Consultando detalhes da chave no banco central...")
        val retorno = this.bcbClient.consultarDetalhesPix(request.pix)
            ?: throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")

        responseObserver?.onNext(retorno.toPixDetalhes())
        responseObserver?.onCompleted()
    }

    override fun buscaInterna(request: PixRequestInterno, responseObserver: StreamObserver<PixDetalhes>?) {

        request.notNulls()

        logger.info("Consultando detalhes da chave no banco de dados...")
        val chave = this.chaveRepository.findByUuid(request.pixId).orElseThrow {
            throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")
        }

        if (chave.client.uuid != request.clienteId){
            throw GrpcExceptionRuntime.notFound("A chave inserida nao pertence ao cliente informado")
        }

        logger.info("Consultando detalhes da chave no banco central...")
        val retorno = this.bcbClient.consultarDetalhesPix(chave.keyPix)
            ?: throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")

        responseObserver?.onNext(retorno.toPixDetalhes(chave.uuid,chave.client.uuid))
        responseObserver?.onCompleted()
    }



}