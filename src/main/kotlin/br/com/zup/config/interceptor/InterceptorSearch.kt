package br.com.zup.config.interceptor

import br.com.zup.config.interceptor.handler.ErrorHandlerSearch
import br.com.zup.enpoint.KeyManagerSearchService
import io.micronaut.aop.InterceptorBean
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandlerSearch::class)
class InterceptorSearch() : Interceptor<KeyManagerSearchService,Any>