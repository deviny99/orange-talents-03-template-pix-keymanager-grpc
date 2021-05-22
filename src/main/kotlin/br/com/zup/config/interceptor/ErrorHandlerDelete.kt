package br.com.zup.config.interceptor

import io.micronaut.aop.Around

@Around
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ErrorHandlerDelete

