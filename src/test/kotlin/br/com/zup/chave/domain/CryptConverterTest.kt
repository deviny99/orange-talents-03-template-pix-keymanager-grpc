package br.com.zup.chave.domain

import br.com.zup.chave.CryptConverter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CryptConverterTest {


    @Test
    fun `Deve encodar e desencodar os dados string`(){

        val valor = "encode"
        val crypt = CryptConverter()
        val encode = crypt.convertToDatabaseColumn(valor)
        Assertions.assertNotEquals(valor,encode)
        val decode = crypt.convertToEntityAttribute(encode)
        Assertions.assertEquals(decode,valor)

    }


}