package br.com.zup.chave.validation

import br.com.zup.TipoChave

interface PixValidation {

    val field:String
    val message:String

     /**
      * Responsavel pela validação da chave do PIX
      * @param chave Chave a ser validada
      * @param tipoChave Tipo da chave
      * @author Marcos Vinicius A. Rocha
      * @return Retornao um Boolean (true -> valido, false -> invalido)
      */
    fun validate(chave:String,tipoChave: TipoChave):Boolean

}