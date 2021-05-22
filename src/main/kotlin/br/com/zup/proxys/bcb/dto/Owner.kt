package br.com.zup.proxys.bcb.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class Owner(@JsonProperty("type") val type: TypePersonProxy,
                 @JsonProperty("name") val name: String,
                 @JsonProperty("taxIdNumber") val taxIdNumber:String)