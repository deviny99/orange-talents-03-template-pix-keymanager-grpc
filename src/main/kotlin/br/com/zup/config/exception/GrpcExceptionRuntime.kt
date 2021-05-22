package br.com.zup.config.exception

import io.grpc.Status

class GrpcExceptionRuntime (msg:String,
                            val status:Status,
                            val fieldsErrors:Map<String,List<String>>? = mapOf())
    : RuntimeException(msg) {

    companion object{

        fun invalidArgument(msg: String, fieldsErrors: Map<String,List<String>> = mapOf()): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.INVALID_ARGUMENT, fieldsErrors)
        }

        fun unknown(msg: String): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.UNKNOWN)
        }

        fun notFound(msg: String): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.NOT_FOUND)
        }

        fun alreadyExists(msg: String): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.ALREADY_EXISTS)
        }

        fun permissionDenied(msg: String): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.PERMISSION_DENIED)
        }

        fun Unauthenticated(msg: String): GrpcExceptionRuntime {
            return GrpcExceptionRuntime(msg, Status.UNAUTHENTICATED)
        }

    }

}