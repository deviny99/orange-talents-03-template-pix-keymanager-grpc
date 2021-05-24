package br.com.zup.config

import br.com.zup.enpoint.KeyManagerDeleteService
import br.com.zup.enpoint.KeyManagerListService
import br.com.zup.enpoint.KeyManagerRegistryService
import br.com.zup.enpoint.KeyManagerSearchService
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
    private lateinit var keyManagerResgistryService: KeyManagerRegistryService
    @field:Inject
    private lateinit var keyManagerDeleteService: KeyManagerDeleteService
    @field:Inject
    private lateinit var keyManagerSearchService: KeyManagerSearchService
    @field:Inject
    private lateinit var keyManagerListService: KeyManagerListService

    override fun onCreated(event: BeanCreatedEvent<ServerBuilder<*>>): ServerBuilder<*> {

        val builder : ServerBuilder<*> = event.bean
        logger.info("Subindo servidor GRPC...")
        logger.info("Subindo endpoints...")
        builder.addService(this.keyManagerResgistryService)
        logger.info("(KeyManagerRegistryService) UP")
        builder.addService(this.keyManagerDeleteService)
        logger.info("(KeyManagerDeleteService) UP")
        builder.addService(this.keyManagerSearchService)
        logger.info("(KeyManagerSearchService) UP")
        builder.addService(this.keyManagerListService)
        logger.info("(KeyManagerListService) UP")
        logger.info("Subindo interceptador...")
        logger.info("Interceptador UP")
        builder.maxInboundMessageSize(1024)
        logger.info("Servidor GRPC Running...")
        return builder
    }
}