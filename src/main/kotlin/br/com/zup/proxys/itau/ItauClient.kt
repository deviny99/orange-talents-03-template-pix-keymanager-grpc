package br.com.zup.proxys.itau

import br.com.zup.TipoConta
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:9091/api/v1")
interface ItauClient {

    @Get(value = "/clientes/{clienteId}/contas")
    fun consultarClient(@PathVariable("clienteId") id:String,@QueryValue("tipo") tipoConta: TipoConta) : ClienteProxyResponse?

}