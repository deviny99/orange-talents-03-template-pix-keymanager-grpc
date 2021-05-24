package br.com.zup.enpoint

import br.com.zup.ClienteRequest
import br.com.zup.KeyManagerListKeysGrpc
import br.com.zup.ListaPix
import br.com.zup.PixDetalhesLista
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.config.interceptor.handler.ErrorHandler
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.enpoint.extensions.toGrpcTimestamp
import br.com.zup.proxys.itau.ItauClient
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class KeyManagerListService(@field:Inject private val chaveRepository: ChaveRepository,
                            @field:Inject private val clientRepository: ClientRepository,
                            @field:Inject private val itauClient: ItauClient)
    : KeyManagerListKeysGrpc.KeyManagerListKeysImplBase() {

    private val logger : Logger = LoggerFactory.getLogger(KeyManagerListService::class.java)

    override fun listagemChaves(request: ClienteRequest, responseObserver: StreamObserver<ListaPix>?) {

        logger.info("Aplicando Validações...")
        request.notNulls()

        logger.info("Consultando dados do cliente...")
        this.itauClient.consultarClient(request.clienteId)?:
        throw GrpcExceptionRuntime.notFound("O id informado não pertence a um cliente Itau")

        logger.info("Buscando chaves do cliente...")
        var lista = listOf<Chave>()
        this.clientRepository.findByUuid(request.clienteId).ifPresent {
             lista = this.chaveRepository.findByClient(it)
        }

        val listaConvertida:List<PixDetalhesLista> = lista.map { chave -> convertPixDetalhes(chave) }
        logger.info("Retornando chaves do cliente :)")
        responseObserver?.onNext(ListaPix
            .newBuilder()
            .addAllChaves(listaConvertida)
            .build())
        responseObserver?.onCompleted()
    }

    fun convertPixDetalhes(chave: Chave): PixDetalhesLista {
        return PixDetalhesLista.newBuilder()
            .setTipoChave(chave.tipoChave)
            .setClienteId(chave.client.uuid)
            .setPixValue(chave.keyPix)
            .setCreatedAt(chave.createdAt.toGrpcTimestamp())
            .setPixID(chave.uuid)
            .setTipoConta(chave.tipoConta)
            .build()
    }
}
