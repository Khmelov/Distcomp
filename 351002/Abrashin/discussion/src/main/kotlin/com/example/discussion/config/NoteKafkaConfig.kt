package com.example.discussion.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteReply
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@EnableKafka
class NoteKafkaConfig(
    @Value("\${note.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${note.kafka.command-group-id}") private val commandGroupId: String,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun kafkaAdmin(): KafkaAdmin = KafkaAdmin(
        mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers)
    ).apply {
        setFatalIfBrokerNotAvailable(false)
    }

    @Bean
    fun inTopic(): NewTopic = TopicBuilder.name(NoteKafkaTopics.IN_TOPIC)
        .partitions(3)
        .replicas(1)
        .build()

    @Bean
    fun outTopic(): NewTopic = TopicBuilder.name(NoteKafkaTopics.OUT_TOPIC)
        .partitions(3)
        .replicas(1)
        .build()

    @Bean
    fun noteReplyProducerFactory(): ProducerFactory<String, NoteReply> =
        DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to "lz4",
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BATCH_SIZE_CONFIG to 32_768,
                ProducerConfig.RETRIES_CONFIG to Int.MAX_VALUE
            ),
            StringSerializer(),
            JsonSerializer<NoteReply>(objectMapper.copy()).apply {
                setAddTypeInfo(false)
            }
        )

    @Bean
    fun noteReplyKafkaTemplate(): KafkaTemplate<String, NoteReply> =
        KafkaTemplate(noteReplyProducerFactory())

    @Bean
    fun noteCommandConsumerFactory(): ConsumerFactory<String, NoteCommand> {
        val deserializer = JsonDeserializer(NoteCommand::class.java, objectMapper.copy(), false).apply {
            addTrustedPackages("*")
            setUseTypeHeaders(false)
        }
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to commandGroupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest"
            ),
            StringDeserializer(),
            deserializer
        )
    }

    @Bean
    fun noteCommandKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, NoteCommand> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, NoteCommand>()
        factory.consumerFactory = noteCommandConsumerFactory()
        factory.setConcurrency(3)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        return factory
    }
}
