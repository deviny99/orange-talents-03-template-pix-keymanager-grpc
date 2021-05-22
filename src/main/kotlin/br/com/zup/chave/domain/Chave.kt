package br.com.zup.chave.domain

import br.com.zup.TipoChave
import br.com.zup.TipoConta
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "chaves")
class Chave(@field:ManyToOne(fetch = FetchType.EAGER,cascade = [CascadeType.PERSIST,CascadeType.MERGE])
            val client: Client,
            @field:Size(max = 77, message = "A chave deve ter no maximo 77 caracteres")
            @field:Column(name="keyPix",nullable = false, length = 77)
            val keyPix:String,
            @Enumerated(EnumType.STRING)
            @field:Column(nullable = false) val tipoChave:TipoChave,
            @Enumerated(EnumType.STRING)
            @field:Column(nullable = false) val tipoConta: TipoConta) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null
    private set
    @Column(nullable = false, updatable = false, unique = true)
    val uuid:String = UUID.randomUUID().toString()

}