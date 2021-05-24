package br.com.zup.enpoint.extensions

import br.com.zup.ClienteRequest
import br.com.zup.config.exception.GrpcExceptionRuntime

fun ClienteRequest.notNulls(){
    if (clienteId.isNullOrBlank()){
        throw GrpcExceptionRuntime.invalidArgument("O id do cliente não pode ser nulo ou vazio")
    }

}