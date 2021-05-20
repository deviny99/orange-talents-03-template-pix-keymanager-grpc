package br.com.zup.chave.domain

import br.com.zup.chave.CryptConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable

@Embeddable
class Instituicao(@field:Column(nullable = false) val nomeInstituicao: String,
                  @field:Column(nullable = false) @field:Convert(converter = CryptConverter::class) val ispb:String) {
}