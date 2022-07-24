package io.lonmstalker.webfluxkafka.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

// https://projectreactor.io/docs/netty/snapshot/reference/index.html
@Configuration(proxyBeanMethods = false)
class WebClientConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        val provider = ConnectionProvider.builder("default")
            .maxConnections(500)
            .pendingAcquireTimeout(Duration.ofMillis(500))
            .evictInBackground(Duration.ofSeconds(120))
            .build()
        val httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
            .doOnConnected { connection ->
                connection
                    .addHandlerLast(ReadTimeoutHandler(2))
                    .addHandlerLast(WriteTimeoutHandler(2))
            }
        return builder
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}