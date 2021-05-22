package br.com.zup.config.interceptor

import br.com.zup.config.exception.GrpcExceptionRuntime
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.validation.ConstraintViolationException

interface Interceptor<M,V> : MethodInterceptor<M,V> {

    override fun intercept(context: MethodInvocationContext<M,V>) : V?{

        try
        {
            return context.proceed()
        }catch (exception: Exception){

            val statusError = when(exception){

                is HttpClientException -> Status.INVALID_ARGUMENT.withDescription(exception.localizedMessage)
                    .asRuntimeException()

                is HttpClientResponseException -> Status.INVALID_ARGUMENT.withDescription(exception.localizedMessage)
                    .asRuntimeException()

                is GrpcExceptionRuntime -> handleStatusRuntimeException(exception)

                is ConstraintViolationException -> handleConstraintValidationException(exception)

                else -> Status.UNKNOWN.withDescription("unexpected error happened").asRuntimeException()
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(statusError)
            return null
        }
    }

    private fun convertFildViolations(map:Map<String,List<String>>): List<BadRequest.FieldViolation>{

        val violations: MutableList<BadRequest.FieldViolation> = mutableListOf()


        map.forEach{

            val field = it.key
            it.value.forEach { valor ->
                violations.add(BadRequest.FieldViolation.newBuilder()
                    .setField(field)
                    .setDescription(valor)
                    .build())
            }
        }
        return violations.toList()
    }

    private fun handleStatusRuntimeException(e: GrpcExceptionRuntime):StatusRuntimeException
    {
        val badRequest = BadRequest.newBuilder()
            .addAllFieldViolations(e.fieldsErrors?.let { convertFildViolations(it) })
            .build()

        println(e.fieldsErrors?.let { this.convertFildViolations(it) })

        val status = com.google.rpc.Status.newBuilder()
            .setCode(e.status.code.value())
            .setMessage(e.localizedMessage)
            .addDetails(com.google.protobuf.Any.pack(badRequest))
            .build()

        return io.grpc.protobuf.StatusProto.toStatusRuntimeException(status)
    }
    private fun handleConstraintValidationException(e:ConstraintViolationException):StatusRuntimeException
    {
        val badRequest = BadRequest.newBuilder() // com.google.rpc.BadRequest
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name) // propertyPath=save.entity.email
                    .setDescription(it.message)
                    .build()
            }
            ).build()

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("request with invalid parameters")
            .addDetails(com.google.protobuf.Any.pack(badRequest)) // com.google.protobuf.Any
            .build()

        return StatusProto.toStatusRuntimeException(statusProto) // io.grpc.protobuf.StatusProto
    }

}