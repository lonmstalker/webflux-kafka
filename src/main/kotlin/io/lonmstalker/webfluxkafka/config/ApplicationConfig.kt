package io.lonmstalker.webfluxkafka.config

import io.lonmstalker.webfluxkafka.config.props.AppProperties
import io.lonmstalker.webfluxkafka.model.TestModel
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues
import java.util.concurrent.Executors

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = [AppProperties::class])
class ApplicationConfig {

    @Bean
    fun kotlinSink(): MutableSharedFlow<TestModel> = MutableSharedFlow()

    @Bean
    fun fluxSink(): Sinks.Many<TestModel> = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE)

    @Bean
    fun executor(): ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
}