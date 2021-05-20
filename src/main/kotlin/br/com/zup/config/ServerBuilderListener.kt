package br.com.zup.config

import br.com.zup.enpoint.KeyManagerEnpoint
import io.grpc.ServerBuilder
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerBuilderListener : BeanCreatedEventListener<ServerBuilder<*>>{

    private val logger : Logger = LoggerFactory.getLogger(ServerBuilder::class.java)

    @field:Inject
    private lateinit var keyManagerEnpoint: KeyManagerEnpoint

    override fun onCreated(event: BeanCreatedEvent<ServerBuilder<*>>): ServerBuilder<*> {

        val builder : ServerBuilder<*> = event.bean
        logger.info("subindo servidor GRPC...")
        logger.info("criando endpoints...")
        builder.addService(this.keyManagerEnpoint)
        logger.info("(KeyManagerEnpoint) UP")
        logger.info("criando interceptador...")
//        builder.intercept(GrpcExceptionHandleer())
//       builder.intercept(GrpcExceptionHandler())
        logger.info("Interceptador criado")
        builder.maxInboundMessageSize(1024)
        logger.info("Servidor GRPC Running...")
        return builder
    }
}