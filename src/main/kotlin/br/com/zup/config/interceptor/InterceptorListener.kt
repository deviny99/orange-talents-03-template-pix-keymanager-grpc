package br.com.zup.config.interceptor

import br.com.zup.config.interceptor.handler.ErrorHandler
import io.micronaut.aop.InterceptorBean
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class InterceptorListener<T> : Interceptor<T,Any>