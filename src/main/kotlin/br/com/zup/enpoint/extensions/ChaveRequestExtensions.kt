package br.com.zup.enpoint.extensions

import br.com.zup.ChaveRequest
import br.com.zup.TipoChave
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.domain.Client
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.chave.validation.PixValidatorEngine
import br.com.zup.config.interceptor.GrpcExceptionRuntime
import java.util.*

/**
 * Valida a chave de acordo com o formato de cada tipo
 * @param validatorEngine engine de validação para rodar as validações
 * @author Marcos Vinicius A. Rocha
 * @exception GrpcExceptionRuntime lança exceção caso tenha algum erro de validação
 */
fun ChaveRequest.validarChavePix(validatorEngine: PixValidatorEngine){
         validatorEngine.verify(this.chave,this.tipo)
}

/**
 * Verifica se o formato do ID do cliente está no formato UUID
 * @author Marcos Vinicius A. Rocha
 * @exception GrpcExceptionRuntime lança exceção caso tenha algum erro de validação
 */
fun ChaveRequest.verificaFormatoId(){
    try {
        UUID.fromString(this.idClient)
    }catch (ex:Exception){
        throw GrpcExceptionRuntime.invalidArgument("O id do cliente está no formato invalido", mapOf())
    }
}

/**
 * Valida os campos que não podem ser nulos
 * @author Marcos Vinicius A. Rocha
 * @exception GrpcExceptionRuntime lança exceção caso tenha algum erro de validação
 */
fun ChaveRequest.notNulls(){
    if (this.idClient.isNullOrBlank())
        throw GrpcExceptionRuntime.invalidArgument("O id do cliente não pode ser nulo", mapOf())
}

/**
 * Verifica se a chave já foi cadastrada
 * @author Marcos Vinicius A. Rocha
 * @param chaveRepository  Interface do repositorio do banco de dados de Chave
 * @exception GrpcExceptionRuntime lança exceção caso já tenha essa chave cadastrada
 */
fun ChaveRequest.verificaDuplicidade(chaveRepository: ChaveRepository){

    if (chaveRepository.existsByKeyPix(this.chave) && this.tipo!=TipoChave.ALEATORIO)
        throw GrpcExceptionRuntime.alreadyExists("essa chave já foi cadastrada.")
}

/**
 * Converte os dados da requisição em objeto de dominio
 * @author Marcos Vinicius A. Rocha
 * @param cliente Cliente dono da chave
 * @param keyRefresh Chave atualizada
 * @exception GrpcExceptionRuntime lança exceção caso tenha algum erro de validação
 * @return retorna um objeto Chave
 */
fun ChaveRequest.toModel(cliente: Client): Chave {

    return Chave(client = cliente,
        keyPix  = chave,
        tipoChave = this.tipo,
        tipoConta = this.tipoConta)
}

fun ChaveRequest.refreshKey(chave: Chave,keyRefresh:String): Chave {
    return Chave(client = chave.client,
        keyPix  = keyRefresh,
        tipoChave = chave.tipoChave,
        tipoConta = chave.tipoConta)
}




