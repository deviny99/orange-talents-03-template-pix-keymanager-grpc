package br.com.zup.config.interceptor.handler

import io.micronaut.aop.Around

@Around
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ErrorHandlerRegistry

