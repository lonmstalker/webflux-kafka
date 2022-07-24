package io.lonmstalker.webfluxkafka.listener

import io.lonmstalker.webfluxkafka.config.props.AppProperties
import io.lonmstalker.webfluxkafka.model.TestModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Component
class SinkListener(
    private val kotlinSink: MutableSharedFlow<TestModel>, private val fluxSink: Sinks.Many<TestModel>,
    private val webClient: WebClient, private val properties: AppProperties
) {

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        this.fluxSink()
        this.kotlinSink()
    }

    private fun fluxSink() = this.fluxSink.asFlux()
        .doOnNext { LOGGER.info(">>>>>>>>>>>>flux before call: ${it.id}, time: ${LocalDateTime.now()}") }
        .concatMap { model ->
            this.sendModelInAnotherService(model).bodyToFlux(String::class.java)
                .doOnNext { LOGGER.info(">>>>>>>>>>>>flux after call: ${model.id}, time: ${LocalDateTime.now()}") }
        }
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe()

    @OptIn(FlowPreview::class)
    private fun kotlinSink() {
        val subscribingScope = CoroutineScope(SupervisorJob())
        this.kotlinSink
            .onEach { LOGGER.info(">>>>>>>>>>>>flow before call: ${it.id}, time: ${LocalDateTime.now()}") }
            .flatMapConcat { model ->
                this.sendModelInAnotherService(model).bodyToFlow<String>()
                    .onEach { LOGGER.info(">>>>>>>>>>>>flow after call: ${model.id}, time: ${LocalDateTime.now()}") }
            }
            .launchIn(subscribingScope)
    }

    private fun sendModelInAnotherService(testModel: TestModel) =
        this.webClient.post()
            .uri(this.properties.secondServiceUrl)
            .body(BodyInserters.fromValue(testModel))
            .retrieve()

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}