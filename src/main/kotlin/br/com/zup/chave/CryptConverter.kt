package br.com.zup.chave

import java.util.*
import javax.persistence.AttributeConverter

class CryptConverter :  AttributeConverter<String,String>{


    override fun convertToDatabaseColumn(attribute: String?): String {
        return Base64.getEncoder().encodeToString(attribute?.toByteArray())
    }

    override fun convertToEntityAttribute(dbData: String?): String {
        return String(Base64.getDecoder().decode(dbData?.toByteArray()))
    }

}