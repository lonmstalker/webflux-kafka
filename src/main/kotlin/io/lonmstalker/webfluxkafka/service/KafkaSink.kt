package io.lonmstalker.webfluxkafka.service

import io.lonmstalker.webfluxkafka.model.TestModel
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux

interface KafkaSink {
    fun listenKafkaFlux(): Flux<TestModel>
    fun listenKafkaFlow(): Flow<TestModel>
}