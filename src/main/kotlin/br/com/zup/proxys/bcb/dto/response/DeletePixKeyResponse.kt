package br.com.zup.proxys.bcb.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class DeletePixKeyResponse(@field:JsonProperty("key") val key:String,
                                @field:JsonProperty("participant") val participant:String,
                                @field:JsonProperty("deletedAt") val deletedAt:LocalDateTime)
