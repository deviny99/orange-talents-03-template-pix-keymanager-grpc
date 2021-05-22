package br.com.zup.enpoint.extensions

import br.com.zup.PixRequestExterno
import br.com.zup.PixRequestInterno
import br.com.zup.config.exception.GrpcExceptionRuntime

fun PixRequestInterno.notNulls(){

    val map : MutableMap<String,MutableList<String>> = mutableMapOf()

    if (pixId.isNullOrBlank()){

        map["pixId"].isNullOrEmpty().let {
            map["pixId"] = mutableListOf()
        }
        map["pixId"]?.add("A chave inserida nao pode ser nula ou vazia")
    }

    if (clienteId.isNullOrBlank()){

        map["clienteId"].isNullOrEmpty().let {
            map["clienteId"] = mutableListOf()
        }
        map["clienteId"]?.add( "O id do Cliente nao pode ser nulo ou vazia")
    }

    if(map.isNotEmpty()){
        throw GrpcExceptionRuntime.invalidArgument("Erro de Validação",map)
    }

}

fun PixRequestExterno.notNulls(){
    if (pix.isNullOrBlank()){
        throw GrpcExceptionRuntime.invalidArgument("A chave inserida nao pode ser nula ou vazia")
    }

}