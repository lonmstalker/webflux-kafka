package io.lonmstalker.webfluxkafka.model

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

@Table("test")
data class TestModel @PersistenceCreator @JsonCreator constructor(
    @Id internal var id: UUID,
    @org.springframework.data.annotation.Transient internal var password: Password,
    @org.springframework.data.annotation.Transient internal var innerModel: InnerTestModel,
    @CreatedDate internal var createdDate: LocalDateTime,
    @LastModifiedDate internal var updatedDate: LocalDateTime,
    @Version internal var version: Int
) {

    companion object {
        data class InnerTestModel @JsonCreator constructor(
            private val id: UUID,
            private val name: String
        )

        @JvmInline
        value class Password @JsonCreator constructor(private val pass: String)
    }
}
