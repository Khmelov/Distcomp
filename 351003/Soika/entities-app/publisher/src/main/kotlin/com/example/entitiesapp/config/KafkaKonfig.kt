package com.example.entitiesapp.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import java.time.Duration

@Configuration
class KafkaConfig {

    @Bean
    fun inTopic(): NewTopic = TopicBuilder.name("InTopic").partitions(3).build()

    @Bean
    fun outTopic(): NewTopic = TopicBuilder.name("OutTopic").partitions(3).build()

    @Bean
    fun replyingTemplate(
        pf: ProducerFactory<String, String>,
        container: ConcurrentMessageListenerContainer<String, String>
    ): ReplyingKafkaTemplate<String, String, String> {
        val template = ReplyingKafkaTemplate(pf, container)
        template.setDefaultReplyTimeout(Duration.ofSeconds(2))
        return template
    }

    @Bean
    fun replyContainer(
        cf: ConsumerFactory<String, String>
    ): ConcurrentMessageListenerContainer<String, String> {
        val containerProperties = ContainerProperties("OutTopic")
        return ConcurrentMessageListenerContainer(cf, containerProperties)
    }
}