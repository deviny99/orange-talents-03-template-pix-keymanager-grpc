package br.com.zup.enpoint.extensions

import br.com.zup.ClienteChaveRequest
import br.com.zup.chave.domain.Chave
import br.com.zup.chave.repository.ChaveRepository
import br.com.zup.config.exception.GrpcExceptionRuntime

/**
 * Valida os campos que não podem ser nulos
 * @author Marcos Vinicius A. Rocha
 * @exception GrpcExceptionRuntime - lança exceção caso tenha algum erro de validação
 */
fun ClienteChaveRequest.notNulls(){

    val invalidsMap : MutableMap<String,MutableList<String>> = mutableMapOf()

    when{
        chave.isNullOrBlank() -> {

            invalidsMap["chave"].isNullOrEmpty().let {
                invalidsMap["chave"] = mutableListOf()
            }
            invalidsMap["chave"]?.add("O campo não pode ser nulo ou vazio.")
        }
        idClient.isNullOrBlank() -> {
            invalidsMap["clienteId"].isNullOrEmpty().let {
                invalidsMap["clientId"] = mutableListOf()
            }
            invalidsMap["clienteId"]?.add("O campo não pode ser nulo ou vazio.")
        }
    }

    if (invalidsMap.isNotEmpty()){
        throw GrpcExceptionRuntime.invalidArgument("Requisição invalida.",invalidsMap.toMap())
    }
}

/**
 * Valida a chave da requisição e retorna um objeto de dominio
 * @author Marcos Vinicius A. Rocha
 * @param chaveRepository - Interface do repositorio do banco de dados de Chave
 * @exception GrpcExceptionRuntime.notFound - lança exceção do status NOT FOUND quando a chave informada não existe
 * no banco de dados
 * @return Retorna objeto de dominio chave através da chave informada no corpo da requisição
 */
fun ClienteChaveRequest.retornarChave(chaveRepository: ChaveRepository) : Chave{
    return chaveRepository.findByKeyPix(this.chave).orElseThrow {
        throw GrpcExceptionRuntime.notFound("Não possivel remover a chave informada, pois não está cadastrada")
    }
}

/**
 * Verifica se o id do cliente passado no corpo da requisição é realmente o dono da chave do pix
 * @author Marcos Vinicius A. Rocha
 * @param pix - Objeto Chave
 * @exception GrpcExceptionRuntime.notFound - lança exceção do status NOT FOUND se o cliente informado não for o dono
 * da chave
 */
fun ClienteChaveRequest.verificaDonoChave(pix: Chave){
    if (pix.client.uuid != this.idClient){
        throw GrpcExceptionRuntime.notFound("A chave informada não está associada com o cliente informado")
    }
}