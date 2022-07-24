package io.lonmstalker.webfluxkafka.event

import io.lonmstalker.webfluxkafka.model.TestModel
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.data.r2dbc.mapping.OutboundRow
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Order(1)
@Component
class TestModelSaveCallback : AfterSaveCallback<TestModel> {

    override fun onAfterSave(entity: TestModel, outboundRow: OutboundRow, table: SqlIdentifier): Publisher<TestModel> {
        LOGGER.info("Model with id: {} saved", entity.id)
        return Mono.justOrEmpty(entity)
    }

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
