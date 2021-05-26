package br.com.zup.enpoint.extensions

import br.com.zup.PixRequest
import br.com.zup.config.exception.GrpcExceptionRuntime
import br.com.zup.enpoint.Filtro

fun PixRequest.filtro():Filtro{

    return when(filtroCase){

       PixRequest.FiltroCase.PIXID -> pixId.let {

            Filtro.PorPixId(clienteId = it.clienteId,pixId = it.pixId)
        }

        PixRequest.FiltroCase.CHAVE -> Filtro.PorChave(chave)
        PixRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()

    }

}

fun PixRequest.notNulls(){

     when(filtroCase) {

        PixRequest.FiltroCase.PIXID -> {
            val map : MutableMap<String,MutableList<String>> = mutableMapOf()
            if (pixId.pixId.isNullOrBlank()){

                map["pixId"].isNullOrEmpty().let {
                    map["pixId"] = mutableListOf()
                }
                map["pixId"]?.add("Nao pode ser nula ou vazio")

            }

            if (pixId.clienteId.isNullOrBlank()){

                map["clienteId"].isNullOrEmpty().let {
                    map["clienteId"] = mutableListOf()
                }
                map["clienteId"]?.add("Nao pode ser nula ou vazio")
            }

            if (map.isNotEmpty()){
                throw GrpcExceptionRuntime.invalidArgument("Erro na requisicao.",map.toMap())
            }
        }

        PixRequest.FiltroCase.CHAVE -> {

            if (chave.isNullOrBlank()){
                throw GrpcExceptionRuntime.invalidArgument("A chave inserida nao pode ser nula ou vazia.")
            }

        }
        PixRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()
    }
}