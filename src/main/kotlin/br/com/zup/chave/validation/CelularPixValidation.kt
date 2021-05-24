package br.com.zup.chave.validation

import br.com.zup.TipoChave
import javax.inject.Singleton

@Singleton
open class CelularPixValidation:PixValidation {

    override val field: String
        get() = TipoChave.CELULAR.name.trim().toLowerCase()
    override val message: String
        get() = "a chave ${this.field} esta no formato invalido.".trim().toLowerCase()


    override fun validate(chave:String,tipoChave: TipoChave): Boolean {
        if (tipoChave != TipoChave.CELULAR){
            return true
        }

        return chave.toLowerCase().matches(Regex("^\\+[1-9}]{2}\\([1-9]{2}\\)(9[1-9])[0-9]{3}\\-[0-9]{4}\$"))
        //return chave.toLowerCase().matches(Regex("^\\+[1-9][0-9]\\d{1,14}\$"))
    }
}