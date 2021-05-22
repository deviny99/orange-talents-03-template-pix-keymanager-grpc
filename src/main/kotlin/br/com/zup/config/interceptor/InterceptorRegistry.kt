package br.com.zup.config.interceptor

import br.com.zup.config.interceptor.handler.ErrorHandlerRegistry
import br.com.zup.enpoint.KeyManagerRegistryService
import io.micronaut.aop.InterceptorBean
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandlerRegistry::class)
class InterceptorRegistry() : Interceptor<KeyManagerRegistryService,Any>