package br.com.zup.chave.validation

import br.com.zup.TipoChave

interface PixValidator {

    val validations: Set<PixValidation>

    val fieldsErrors: Map<String, List<String>>

    /**
     * Responsavel por rodar as validações que foram passadas na lista pelo construtor
     * @param chave Chave a ser validada
     * @param tipoChave Tipo da chave
     * @author Marcos Vinicius A. Rocha
     */
    fun verify(chave:String,tipoChave: TipoChave):Unit


}