package io.lonmstalker.webfluxkafka.service.impl

import io.lonmstalker.webfluxkafka.model.TestModel
import io.lonmstalker.webfluxkafka.repository.TestFluxRepository
import io.lonmstalker.webfluxkafka.repository.TestKotlinRepository
import io.lonmstalker.webfluxkafka.service.KafkaSink
import io.lonmstalker.webfluxkafka.service.TestService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.*
import java.util.function.Supplier

@Service
class TestServiceImpl(
    private val testKotlinRepository: TestKotlinRepository,
    private val testFluxRepository: TestFluxRepository,
    private val kotlinSink: MutableSharedFlow<TestModel>,
    private val fluxSink: Sinks.Many<TestModel>,
    private val dispatcher: ExecutorCoroutineDispatcher
) : TestService, KafkaSink {

    override fun listenKafkaFlux(): Flux<TestModel> =
        this.fluxSink.asFlux()

    override fun listenKafkaFlow(): Flow<TestModel> =
        this.kotlinSink.asSharedFlow()

    override suspend fun save(testModel: TestModel): TestModel =
        this.testKotlinRepository.save(testModel)

    override suspend fun findAllFlow(): Flow<TestModel> =
        this.testKotlinRepository.findAll()
            .map {
                it.copy(
                    password = addPassword(UUID.randomUUID().toString()),
                    innerModel = addInnerModel("ok:${UUID.randomUUID()}")
                )
            }
            .buffer(BUFFER_SIZE)
            .onEach { kotlinSink.emit(it) }

    override suspend fun findAllCoroutine(): Collection<TestModel> {
        return this.testKotlinRepository.findAll()
            .map {
                callAsync {
                    it.copy(
                        password = addPassword(UUID.randomUUID().toString()),
                        innerModel = addInnerModel("ok:${UUID.randomUUID()}")
                    )
                }
            }
            .buffer(BUFFER_SIZE)
            .map { it.await() }
            .onEach { kotlinSink.emit(it) }
            .toList()
    }

    override fun findAllFlux(): Flux<TestModel> =
        this.testFluxRepository.findAll()
            .map {
                it.copy(
                    password = addPassword(UUID.randomUUID().toString()),
                    innerModel = addInnerModel("ok:${UUID.randomUUID()}")
                )
            }
            .onBackpressureBuffer(BUFFER_SIZE)
            .doOnNext { fluxSink.tryEmitNext(it) }

    private suspend fun <T> callAsync(func: Supplier<T>) =
        CoroutineScope(dispatcher).async { func.get() }

    companion object {
        @JvmStatic
        private val BUFFER_SIZE = 10

        @JvmStatic
        private fun addPassword(pass: String): TestModel.Companion.Password = TestModel.Companion.Password(pass)

        @JvmStatic
        private fun addInnerModel(name: String): TestModel.Companion.InnerTestModel =
            TestModel.Companion.InnerTestModel(id = UUID.randomUUID(), name = name)
    }
}