package br.com.zup.proxys.bcb.dto

import br.com.zup.TipoChave

enum class KeyTypeProxy(private val tipoChave: TipoChave?) {

    CPF(TipoChave.CPF),
    CNPJ(null),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIO)

}