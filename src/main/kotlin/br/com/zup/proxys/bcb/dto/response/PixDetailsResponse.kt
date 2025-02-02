package br.com.zup.proxys.bcb.dto.response

import br.com.zup.PixDetalhes
import br.com.zup.Titular
import br.com.zup.enpoint.extensions.toGrpcTimestamp
import br.com.zup.proxys.bcb.dto.BankAccount
import br.com.zup.proxys.bcb.dto.KeyTypeProxy
import br.com.zup.proxys.bcb.dto.Owner
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class PixDetailsResponse(@JsonProperty("keyType") val keyType:KeyTypeProxy,
                              @JsonProperty("key") val key:String,
                              @JsonProperty("bankAccount") val bankAccount:BankAccount,
                              @JsonProperty("owner") val owner: Owner,
                              @JsonProperty("createdAt") val createdAt:LocalDateTime) {


    fun toPixDetalhes(pixId:String,clientId:String):PixDetalhes{
        return PixDetalhes.newBuilder()
            .setPixId(pixId)
            .setClienteId(clientId)
            .setTipoChave(keyType.tipoChave)
            .setPixValue(key)
            .setCreatedAt(createdAt.toGrpcTimestamp())
            .setTitular(Titular.newBuilder()
                .setNome(owner.name)
                .setCpf(owner.taxIdNumber)
                .build())
            .setInstituicao(PixDetalhes.InstituicaoInfo.newBuilder()
                .setIspb(bankAccount.participant)
                .setAgencia(bankAccount.branch)
                .setNumero(bankAccount.accountNumber)
                .setTipoConta(bankAccount.accountType.convertTipoConta())
                .build())
            .build()
    }


}

