package br.com.zup.enpoint

import br.com.zup.ChaveRequest
import br.com.zup.ChaveResponse
import br.com.zup.KeyManagerRegistryGrpc
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.validation.PixValidation
import br.com.zup.chave.validation.PixValidatorEngine
import br.com.zup.config.interceptor.handler.ErrorHandlerRegistry
import br.com.zup.enpoint.extensions.notNulls
import br.com.zup.enpoint.extensions.validarChavePix
import br.com.zup.enpoint.extensions.verificaDuplicidade
import br.com.zup.enpoint.extensions.verificaFormatoId
import br.com.zup.enpoint.service.RegistroChave
import io.grpc.stub.StreamObserver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandlerRegistry
open class KeyManagerRegistryService(
    @field:Inject private val chaveRepository: ChaveRepository,
    @field:Inject private val validations:Set<PixValidation>,
    @field:Inject private val resgistroChave: RegistroChave
) : KeyManagerRegistryGrpc.KeyManagerRegistryImplBase() {

    private val logger : Logger = LoggerFactory.getLogger(KeyManagerRegistryService::class.java)

    override fun cadastrarChave(request: ChaveRequest, responseObserver: StreamObserver<ChaveResponse>?) {

        request.notNulls()
        request.verificaFormatoId()
        request.validarChavePix(PixValidatorEngine(this.validations))
        request.verificaDuplicidade(this.chaveRepository)

        val uuidChave = this.resgistroChave.registrar(request)
        logger.info("Chave salva no banco de dados :)")

        responseObserver?.onNext(ChaveResponse.newBuilder()
            .setId(uuidChave)
            .build())
        responseObserver?.onCompleted()
    }

}