package io.lonmstalker.webfluxkafka.listener

import io.lonmstalker.webfluxkafka.model.TestModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers

@Component
class SinkListener(
    private val kotlinSink: MutableSharedFlow<TestModel>,
    private val fluxSink: Sinks.Many<TestModel>,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        this.fluxSink()
        this.kotlinSink
    }

    private fun fluxSink() = this.fluxSink.asFlux()
        .doOnNext { LOGGER.info(">>>>>>>>>>>>next flux: ${it.id}") }
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe()

    private fun kotlinSink() {
        val subscribingScope = CoroutineScope(SupervisorJob())
        this.kotlinSink
            .onEach { LOGGER.info(">>>>>>>>>>>>next kotlin: ${it.id}") }
            .launchIn(subscribingScope)
    }

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}