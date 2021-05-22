package br.com.zup.config.interceptor

import br.com.zup.enpoint.KeyManagerDeleteService
import io.micronaut.aop.InterceptorBean
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandlerDelete::class)
class InterceptorDelete : Interceptor<KeyManagerDeleteService,Any>
