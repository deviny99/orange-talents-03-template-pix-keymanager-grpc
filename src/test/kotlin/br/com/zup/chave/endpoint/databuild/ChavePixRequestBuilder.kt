package br.com.zup.chave.endpoint.databuild


import br.com.zup.PixRequestExterno
import br.com.zup.PixRequestInterno
import br.com.zup.chave.domain.Chave
import java.util.*

class ChavePixRequestBuilder {

    companion object{

        fun requestInternoPixAleatorioValido(chave:Chave):PixRequestInterno{
            return PixRequestInterno.newBuilder()
                .setPixId(chave.uuid)
                .setClienteId(clientIdDefault())
                .build()
        }

        fun requestExternoPixAleatorioValido():PixRequestExterno{
            return PixRequestExterno.newBuilder()
                .setPix(pixDefault())
                .build()
        }

        fun requestInternoPixAleatorioNulo():PixRequestInterno{
            return PixRequestInterno.newBuilder()
                .build()
        }

        fun requestExternoPixAleatorioNulo():PixRequestExterno{
            return PixRequestExterno.newBuilder()
                .build()
        }

        fun requestInternoPixAleatorioVazio():PixRequestInterno{
            return PixRequestInterno.newBuilder()
                .setPixId("")
                .setClienteId("")
                .build()
        }

        fun requestExternoPixAleatorioVazio():PixRequestExterno{
            return PixRequestExterno.newBuilder()
                .setPix("")
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