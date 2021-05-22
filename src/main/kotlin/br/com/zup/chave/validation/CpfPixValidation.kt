package br.com.zup.chave.validation

import br.com.zup.TipoChave
import javax.inject.Singleton

@Singleton
open class CpfPixValidation : PixValidation {

    override val field: String
        get() = TipoChave.CPF.name.trim().toLowerCase()
    override val message: String
        get() = "a chave ${this.field} esta no formato invalido.".trim().toLowerCase()


    override fun validate(chave:String,tipoChave: TipoChave): Boolean {
        if (tipoChave!=TipoChave.CPF){
            return true
        }

        return chave.replace(".","")
            .replace("-","")
            .toLowerCase()
            .matches(Regex("^[0-9]{11}\$"))
    }
}