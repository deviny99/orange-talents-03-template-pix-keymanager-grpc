package br.com.zup.chave.validation

import br.com.zup.TipoChave
import javax.inject.Singleton

@Singleton
open class EmailPixValidation : PixValidation {

    override val field: String
        get() = TipoChave.EMAIL.name.trim().toLowerCase()

    override val message: String
        get() = "a chave do tipo ${this.field} está no formato invalido.".trim().toLowerCase()


    override fun validate(chave:String,tipoChave: TipoChave): Boolean {

        if (tipoChave !=TipoChave.EMAIL){
            return true
        }

        return chave.toLowerCase()
            .matches(Regex("[a-zA-Z0-9_.]+@[a-zA-Z0-9]+.[a-zA-Z]{2,3}[.] {0,1}[a-zA-Z]+"))
    }
}