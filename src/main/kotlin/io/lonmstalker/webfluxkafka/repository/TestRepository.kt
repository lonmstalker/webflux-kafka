package io.lonmstalker.webfluxkafka.repository

import io.lonmstalker.webfluxkafka.model.TestModel
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface TestKotlinRepository : CoroutineSortingRepository<TestModel, UUID>

interface TestFluxRepository : ReactiveCrudRepository<TestModel, UUID>