package br.com.zup.chave.repository.extensions

import br.com.zup.ChaveRequest
import br.com.zup.chave.domain.Client
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.enpoint.KeyManagerRegistryService
import br.com.zup.enpoint.extensions.refreshKey
import br.com.zup.enpoint.extensions.toModel
import br.com.zup.proxys.bcb.BcbClient
import br.com.zup.proxys.bcb.dto.request.CreatePixKeyRequestProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter
import javax.transaction.Transactional

@Transactional
fun ChaveRepository.registrarChaveBcb(request: ChaveRequest,
                                      client: Client,
                                      bcbClient: BcbClient):String{

    val logger : Logger = LoggerFactory.getLogger(KeyManagerRegistryService::class.java)

    val chave = request.toModel(client)

    logger.info("Registrando chave no sistema do Banco Central")

    val responseBcb = bcbClient.cadastrarChavePix(CreatePixKeyRequestProxy(chave))
        ?:throw GrpcExceptionRuntime.notFound("Não foi possivel registrar a chave")

    logger.info("Chave registrada :) ás ${responseBcb.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))}")
    logger.info("Atualizando as chave e salvando no banco de dados...")
    return this.save(request.refreshKey(chave,responseBcb.key)).uuid
}