package br.com.zup.config.interceptor


import br.com.zup.enpoint.KeyManagerEnpoint
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.JSONPObject
import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.lang.Exception
import java.nio.Buffer
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class Interceptor : MethodInterceptor<KeyManagerEnpoint,Any> {
    override fun intercept(context: MethodInvocationContext<KeyManagerEnpoint, Any>): Any? {

        try
        {
             context.proceed()
        }catch (exception:HttpClientResponseException){

            val response = context.parameterValues[1] as StreamObserver<*>
            response.onError(Status.INVALID_ARGUMENT.withDescription(exception.localizedMessage)
                .asRuntimeException())
        }
        catch (exception: GrpcExceptionRuntime){
            exception.message
            val map:Map<String,Any?> = mapOf(
                Pair("message",exception.message),
                Pair("fields",exception.fieldsErrors))

            val response = context.parameterValues[1] as StreamObserver<*>

            response.onError(exception.status.withDescription(map["message"].toString()).augmentDescription(ObjectMapper()
                .writeValueAsString(map["fields"]))
                .asRuntimeException())
        }

        return null

    }


}


