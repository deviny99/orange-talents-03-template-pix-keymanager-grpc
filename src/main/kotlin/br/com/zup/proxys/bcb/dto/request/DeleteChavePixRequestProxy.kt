package br.com.zup.proxys.bcb.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class DeleteChavePixRequestProxy(@field:JsonProperty("key") val key:String,
                                      @field:JsonProperty("participant") val participant:String)