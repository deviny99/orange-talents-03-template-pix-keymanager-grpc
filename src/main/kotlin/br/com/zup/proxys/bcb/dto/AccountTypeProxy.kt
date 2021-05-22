package br.com.zup.proxys.bcb.dto

import br.com.zup.TipoConta

enum class AccountTypeProxy(private val tipoConta: TipoConta) {

    CACC(TipoConta.CONTA_CORRENTE),
    SVGS(TipoConta.CONTA_POUPANCA);

    fun convertTipoConta():TipoConta{
        return this.tipoConta
    }
}