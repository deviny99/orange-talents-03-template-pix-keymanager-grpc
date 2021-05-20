package br.com.zup.chave.validation

import br.com.zup.TipoChave
import javax.inject.Singleton

@Singleton
open class CelularPixValidation:PixValidation {

    override val field: String
        get() = TipoChave.CELULAR.name.trim().toLowerCase()
    override val message: String
        get() = "a chave ${this.field} está no formato invalido.".trim().toLowerCase()


    override fun validate(chave:String,tipoChave: TipoChave): Boolean {
        if (tipoChave != TipoChave.CELULAR){
            return true
        }


        return chave.replace("-","")
            .replace("(","")
            .replace(")","").toLowerCase().matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))
    }
}