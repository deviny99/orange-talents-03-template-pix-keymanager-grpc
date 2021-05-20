package br.com.zup.enpoint

import br.com.zup.ChaveRequest
import br.com.zup.ChaveResponse
import br.com.zup.KeyManagerServiceGrpc
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import br.com.zup.chave.validation.PixValidation
import br.com.zup.chave.validation.PixValidatorEngine
import br.com.zup.config.interceptor.ErrorHandler
import br.com.zup.config.interceptor.GrpcExceptionRuntime
import br.com.zup.enpoint.extensions.*
import br.com.zup.proxys.itau.ItauClient
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
@ErrorHandler
class KeyManagerEnpoint(
    private val chaveRepository: ChaveRepository,
    @field:Inject private val validations:Set<PixValidation>,
    private val itauClient: ItauClient,
    private val clienteRepository: ClientRepository
) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    private val logger : Logger = LoggerFactory.getLogger(KeyManagerEnpoint::class.java)

    @Transactional
    override fun cadastrarChave(request: ChaveRequest, responseObserver: StreamObserver<ChaveResponse>?) {

        request.notNulls()
        request.verificaFormatoId()
        request.validarChavePix(PixValidatorEngine(this.validations))
        request.verificaDuplicidade(this.chaveRepository)

        val responseClient = itauClient.consultarClient(id = request.idClient,request.tipoConta)
            ?:throw GrpcExceptionRuntime.notFound("Não existe um cliente com o id informado")

        var clienteEntity = responseClient.toModel()

        this.clienteRepository.findByUuid(clienteEntity.uuid).ifPresent{ client ->
                clienteEntity = client
        }

        val chaveEntity = this.chaveRepository.save(request.toModel(clienteEntity))

        responseObserver?.onNext(ChaveResponse.newBuilder()
            .setId(chaveEntity.uuid)
            .build())
        responseObserver?.onCompleted()
    }





}