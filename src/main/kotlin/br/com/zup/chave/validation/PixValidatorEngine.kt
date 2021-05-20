package br.com.zup.chave.validation

import br.com.zup.TipoChave
import br.com.zup.config.interceptor.GrpcExceptionRuntime
/**
 * Classe responsavel por rodar as validações
 * @param validations Validações do tipo PixValidation
 * @author Marcos Vinicius A. Rocha
 */
open class PixValidatorEngine(override val validations: Set<PixValidation>) : PixValidator {

    private val fieldErrorMap : MutableMap<String,MutableList<String>> = mutableMapOf()

    override val fieldsErrors: Map<String, List<String>>
        get() =  this.fieldErrorMap.toMap()

    override fun verify(chave:String,tipoChave: TipoChave) {

        if (validations.isNotEmpty())
        {
            validations.forEach{ validation ->
                if (!validation.validate(chave,tipoChave)){

                    if (fieldErrorMap[validation.field] == null){
                        fieldErrorMap[validation.field] = mutableListOf()
                    }

                    fieldErrorMap[validation.field]?.add(validation.message)
                }
            }
        }

        if (this.fieldErrorMap.isNotEmpty()){
            throw GrpcExceptionRuntime.invalidArgument("Erro de validação",this.fieldsErrors)
        }
    }

}