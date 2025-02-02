package br.com.zup.proxys.bcb.dto.request

import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.domain.Chave
import br.com.zup.proxys.bcb.dto.*
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class CreatePixKeyRequestProxy(@JsonProperty("keyType") val keyType: KeyTypeProxy,
                                    @JsonProperty("key") val key:String?,
                                    @JsonProperty("bankAccount") val bankAccount: BankAccount,
                                    @JsonProperty("owner") val owner: Owner
){

    constructor(chave:Chave) : this(

        chave.tipoChave.convert(),
        chave.keyPix,
        BankAccount(chave.client.instituicao.ispb,
                            chave.client.conta.agencia,
                            chave.client.conta.numero,
                            chave.tipoConta.convert()),
        Owner(TypePersonProxy.NATURAL_PERSON,
        chave.client.nome,
        chave.client.cpf)
    )

}

fun TipoChave.convert(): KeyTypeProxy {
    return when(this){
        TipoChave.CPF -> KeyTypeProxy.CPF
        TipoChave.EMAIL -> KeyTypeProxy.EMAIL
        TipoChave.CELULAR -> KeyTypeProxy.PHONE
        else -> KeyTypeProxy.RANDOM
    }
}


fun TipoConta.convert(): AccountTypeProxy {
    return when(this){
        TipoConta.CONTA_POUPANCA -> AccountTypeProxy.SVGS
        else -> AccountTypeProxy.CACC
    }
}