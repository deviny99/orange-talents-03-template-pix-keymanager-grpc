package br.com.zup.enpoint.service

import br.com.zup.ChaveRequest
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.repository.ClientRepository
import br.com.zup.chave.repository.extensions.registrarChaveBcb
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.itau.ItauClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class RegistroChave(@field:Inject private val chaveRepository: ChaveRepository,
                         @field:Inject private val clienteRepository: ClientRepository,
                         @field:Inject private val itauClient: ItauClient,
                         @field:Inject private val bcbClient: BcbClient) {

    private val logger : Logger = LoggerFactory.getLogger(RegistroChave::class.java)

    @Transactional
    open fun registrar(request: ChaveRequest) : String{

            logger.info("Consultando dados do cliente...")
            val clienteResponseItau = itauClient.consultarClient(id = request.idClient, request.tipoConta)
                ?: throw GrpcExceptionRuntime.notFound("Cliente ou Conta invalido")

            val cliente = this.clienteRepository.findByUuid(request.idClient)
                .orElse(clienteResponseItau.toModel())

            return this.chaveRepository.registrarChaveBcb(request, cliente, this.bcbClient)
    }

}