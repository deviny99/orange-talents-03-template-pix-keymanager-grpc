package br.com.zup.chave.domain

import br.com.zup.chave.CryptConverter
import javax.persistence.*

@Entity
@Table(name = "clientes")
class Client(@field:Column(nullable = false) val uuid: String,
             @field:Column(nullable = false) val nome:String,
             @field:Column(nullable = false) @field:Convert(converter = CryptConverter::class) val cpf:String,
             @field:Embedded val instituicao: Instituicao,
             @field:Embedded val conta: Conta) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null
}
