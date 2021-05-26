package br.com.zup.chave.endpoint.databuild

import br.com.zup.PixRequest
import br.com.zup.chave.domain.Chave

class ChavePixRequestBuilder {

    companion object{

        fun requestInternoPixAleatorioValido(chave:Chave):PixRequest{
            return PixRequest.newBuilder()
                .setPixId(PixRequest
                    .FiltroPorPixId
                    .newBuilder()
                    .setPixId(chave.uuid)
                    .setClienteId(clientIdDefault()))
                .build()
        }

        fun requestExternoPixAleatorioValido():PixRequest{
            return PixRequest.newBuilder()
                .setChave(pixDefault())
                .build()
        }

        fun requestInternoPixAleatorioNulo():PixRequest{
            return PixRequest.newBuilder()
                .setPixId(PixRequest
                    .FiltroPorPixId
                    .newBuilder()
                    .setPixId("")
                    .setClienteId(""))
                .build()
        }

        fun requestExternoPixAleatorioNulo():PixRequest{
            return PixRequest.newBuilder()
                .setChave("")
                .build()
        }

        fun requestInternoPixAleatorioVazio():PixRequest{
            return PixRequest.newBuilder()
                .setPixId(PixRequest
                    .FiltroPorPixId
                    .newBuilder()
                    .setPixId("")
                    .setClienteId(clientIdDefault()))
                .build()
        }

        fun requestExternoPixAleatorioVazio():PixRequest{
            return PixRequest.newBuilder()
                .setChave("")
                .build()
        }

        fun pixDefault():String{
            return "233bc3f2-bad4-11eb-8529-0242ac130003"
        }

        fun clientIdDefault():String{
            return "29860cfe-bad4-11eb-8529-0242ac130003"
        }

    }
}