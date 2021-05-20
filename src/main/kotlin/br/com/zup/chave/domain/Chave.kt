package br.com.zup.chave.domain

import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.config.interceptor.GrpcExceptionRuntime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "chaves")
class Chave(@field:ManyToOne(fetch = FetchType.EAGER,cascade = [CascadeType.PERSIST,CascadeType.MERGE])
            val client: Client,
            @field:kotlin.jvm.Transient @field:Size(max = 77) val keyValue:String?,
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

    @field:Column(name="keyPix",nullable = false, length = 77)
    var keyPix:String
        private set

    init {

        if(this.tipoChave == TipoChave.ALEATORIO){
            this.keyPix = UUID.randomUUID().toString()
        }else
            if (!keyValue.isNullOrBlank()){
               this.keyPix = keyValue
            }else{
                throw GrpcExceptionRuntime.invalidArgument("A chave não deve ser nula ou vazia", mapOf())
            }
    }
}