package br.com.zup.proxys.bcb.dto.response

import br.com.zup.Instituicao
import br.com.zup.PixDetalhes
import br.com.zup.Titular
import br.com.zup.proxys.bcb.dto.BankAccount
import br.com.zup.proxys.bcb.dto.KeyTypeProxy
import br.com.zup.proxys.bcb.dto.Owner
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.protobuf.Timestamp
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import java.time.ZoneId

@Introspected
data class PixDetailsResponse(@JsonProperty("keyType") val keyType:KeyTypeProxy,
                              @JsonProperty("key") val key:String,
                              @JsonProperty("bankAccount") val bankAccount:BankAccount,
                              @JsonProperty("owner") val owner: Owner,
                              @JsonProperty("createdAt") val createdAt:LocalDateTime) {


    fun toPixDetalhes():PixDetalhes{
        return PixDetalhes.newBuilder()
            .setTipoChave(keyType.tipoChave)
            .setPixValue(key)
            .setCreatedAt(createdAt.toGrpcTimestamp())
            .setTitular(Titular.newBuilder()
                                .setNome(owner.name)
                                .setCpf(owner.taxIdNumber)
                        .build())
            .setInstituicao(Instituicao.newBuilder()
                        .setNome(bankAccount.participant)
                        .setAgencia(bankAccount.branch)
                        .setNumero(bankAccount.accountNumber)
                        .setTipoConta(bankAccount.accountType.convertTipoConta())
                        .build())
            .build()
    }

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
            .setInstituicao(Instituicao.newBuilder()
                .setNome(bankAccount.participant)
                .setAgencia(bankAccount.branch)
                .setNumero(bankAccount.accountNumber)
                .setTipoConta(bankAccount.accountType.convertTipoConta())
                .build())
            .build()
    }

    fun LocalDateTime.toGrpcTimestamp(): Timestamp {
        val instant = this.atZone(ZoneId.of("UTC")).toInstant()
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()
    }
}