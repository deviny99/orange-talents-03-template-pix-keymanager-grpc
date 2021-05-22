package br.com.zup.chave.endpoint.databuild

import br.com.zup.ChaveRequest
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import java.util.*

class ChaveRequestBuilder {

    companion object {

        fun requestCpfValido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("111.111.111-11")
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        fun requestEmailValido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("email@test.com")
                .setTipo(TipoChave.EMAIL)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        fun requestCelularValido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("+55(19)99999-9999")
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        fun requestTipoAleatorioNaoNuloJaExistente():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("+55(19)99999-9999")
                .setTipo(TipoChave.ALEATORIO)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        fun requestCpfInvalido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("abc.def.111-8a")
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestCelularInvalido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("(19)12345678")
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestEmailInvalido():ChaveRequest {
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("email_test.com")
                .setTipo(TipoChave.EMAIL)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestIdClientNulo():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setChave("111.111.111-11")
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestChaveAleatorio():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setTipo(TipoChave.ALEATORIO)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestIdClientFormatoInvalido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient("ID INVALIDO")
                .setChave("111.111.111-11")
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }
        fun requestChaveDeTipoInvalido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave("111.111.111-11")
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        fun requestChaveVaziaDoTipoNaoAleatorio():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setTipo(TipoChave.CELULAR)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }


        fun requestChaveComTamanhoInvalido():ChaveRequest{
            return ChaveRequest.newBuilder()
                .setIdClient(UUID.randomUUID().toString())
                .setChave(chaveLimiteCaracteres())
                .setTipo(TipoChave.CPF)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        }

        private fun chaveLimiteCaracteres():String{
            var sb = StringBuffer()
            for (i:Int in 0..78){
                sb.append("1")
            }
            return sb.toString()
        }

    }




}
