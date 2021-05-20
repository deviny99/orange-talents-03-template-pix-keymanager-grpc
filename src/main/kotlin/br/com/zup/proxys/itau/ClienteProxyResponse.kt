package br.com.zup.proxys.itau

import br.com.zup.TipoConta
import br.com.zup.chave.domain.Client
import br.com.zup.chave.domain.Conta
import br.com.zup.chave.domain.Instituicao
import io.micronaut.core.annotation.Introspected

@Introspected
data class ClienteProxyResponse(val titular:TitularProxyDto,
                                val tipo:TipoConta,
                                val agencia:String,
                                val numero:String,
                                val instituicao:InstituicaoProxyDTO){

    fun toModel():Client{
        return Client(this.titular.id,
            this.titular.nome,
            this.titular.cpf,
            Instituicao(this.instituicao.nome,
                this.instituicao.ispb),
            Conta(this.agencia,this.numero))
    }

    data class InstituicaoProxyDTO(val nome:String,
                                   val ispb:String)

    data class TitularProxyDto(val id:String,
                               val nome:String,
                               val cpf: String)

}
