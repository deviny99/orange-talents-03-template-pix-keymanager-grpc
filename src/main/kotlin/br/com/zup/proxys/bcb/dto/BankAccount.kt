package br.com.zup.proxys.bcb.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BankAccount(@JsonProperty("participant") val participant:String,
                       @JsonProperty("branch") val branch:String,
                       @JsonProperty("accountNumber") val accountNumber:String,
                       @JsonProperty("accountType")val accountType: AccountTypeProxy
)