package br.com.zup.proxys.bcb

import br.com.zup.proxys.bcb.dto.request.CreatePixKeyRequestProxy
import br.com.zup.proxys.bcb.dto.response.CreatePixKeyResponse
import br.com.zup.proxys.bcb.dto.request.DeleteChavePixRequestProxy
import br.com.zup.proxys.bcb.dto.response.DeletePixKeyResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:8082/api/v1")
interface BcbClient {

    /**
     * Cadastra a chave do pix no sistema do BCB
     * @author Marcos Vinicius A. Rocha
     * @param bcbRequest - Objeto que representa o xml esperado no sistema alvo
     * @return Retorna um objeto do tipo CreatePixKeyResponseProxy(pode retornar um null)
     */
    @Post(value = "/pix/keys")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun cadastrarChavePix(@Body createPixKeyRequestProxy: CreatePixKeyRequestProxy): CreatePixKeyResponse?

    @Delete(value = "pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deletarChavePix(@PathVariable("key") key: String, @Body deleteChavePixRequestProxy: DeleteChavePixRequestProxy)
    : DeletePixKeyResponse?

}