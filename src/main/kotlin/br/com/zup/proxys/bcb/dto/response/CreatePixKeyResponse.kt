package br.com.zup.proxys.bcb.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class CreatePixKeyResponse(@JsonProperty("key") val key:String,
                                @JsonProperty("createdAt") val createdAt:LocalDateTime)
