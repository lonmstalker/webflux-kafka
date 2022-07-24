package io.lonmstalker.webfluxkafka.service

import io.lonmstalker.webfluxkafka.model.TestModel
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Flux

interface TestService {
    suspend fun save(testModel: TestModel) : TestModel
    suspend fun findAllFlow(): Flow<TestModel>
    suspend fun findAllCoroutine(): Collection<TestModel>
    fun findAllFlux(): Flux<TestModel>
}