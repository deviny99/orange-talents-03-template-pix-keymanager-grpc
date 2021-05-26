package br.com.zup.enpoint

import br.com.zup.PixDetalhes
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.proxys.bcb.BcbClient
import io.micronaut.core.annotation.Introspected
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Introspected
sealed class Filtro{

     val logger : Logger = LoggerFactory.getLogger(KeyManagerSearchService::class.java)
    abstract fun filtrar(repository: ChaveRepository,bcbClient: BcbClient):PixDetalhes?

    @Introspected
    data class PorPixId(val clienteId: String,val pixId: String):Filtro(){
        override fun filtrar(repository: ChaveRepository, bcbClient: BcbClient): PixDetalhes? {

            logger.info("Aplicando validações...")
            logger.info("Consultando detalhes da chave no banco central...")

            val chave = repository.findByUuid(pixId)
                .filter{it.isPertenceAoCliente(clienteId)}
                .orElseThrow{
                  throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")
                }

            logger.info("Consultando detalhes da chave no banco central...")

            return bcbClient.consultarDetalhesPix(chave.keyPix)?.toPixDetalhes(chave.uuid,chave.client.uuid)
        }
    }

    @Introspected
    data class PorChave(val chave: String):Filtro(){
        override fun filtrar(repository: ChaveRepository, bcbClient: BcbClient): PixDetalhes? {

            logger.info("Aplicando validações...")
            logger.info("Consultando detalhes da chave no banco central...")
            return bcbClient.consultarDetalhesPix(chave)?.toPixDetalhes("","")
        }
    }

    @Introspected
    class Invalido:Filtro(){

        override fun filtrar(repository: ChaveRepository, bcbClient: BcbClient): PixDetalhes? {
            throw GrpcExceptionRuntime.notFound("A chave inserida nao esta cadastrada como uma chave PIX")
        }

    }

}
