package io.lonmstalker.webfluxkafka.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class AppProperties constructor(
    internal val secondServiceUrl: String
)