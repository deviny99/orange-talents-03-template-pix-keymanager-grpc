package br.com.zup.chave.endpoint.databuild

import br.com.zup.ClienteChaveRequest

class ClienteChaveRequestBuilder {


    companion object{

        fun requestClientChaveValido(idCliente:String,keyPix:String):ClienteChaveRequest{
            return ClienteChaveRequest
                .newBuilder()
                .setIdClient(idCliente)
                .setChave(keyPix)
                .build()
        }

    }
}