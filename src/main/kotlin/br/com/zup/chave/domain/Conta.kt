package br.com.zup.chave.domain

import br.com.zup.chave.CryptConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
class Conta(@field:Column(nullable = false) @field:Convert(converter = CryptConverter::class)val agencia:String,
            @field:Column(nullable = false) @field:Convert(converter = CryptConverter::class) val numero:String) {


}