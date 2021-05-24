package br.com.zup.proxys.itau

import io.micronaut.core.annotation.Introspected

@Introspected
data class DadosClienteProxyResponse(val id:String,
                                     val nome:String,
                                     val cpf:String,
                                     val instituicao:InstituicaoProxyDTO){

    data class InstituicaoProxyDTO(val nome:String,
                                   val ispb:String)

}
