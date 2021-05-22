package br.com.zup.config.interceptor

import br.com.zup.enpoint.KeyManagerRegistryService
import io.micronaut.aop.InterceptorBean
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class InterceptorRegistry() : Interceptor<KeyManagerRegistryService,Any>