package com.example.discussion.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.example.discussion.model.NoteEntity
import com.example.discussion.model.NoteKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.CassandraContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.InetSocketAddress
import java.time.Duration

@Testcontainers
@SpringBootTest(
    properties = [
        "spring.cassandra.schema-action=NONE",
        "spring.liquibase.enabled=false",
        "discussion.cassandra.warmup.enabled=false"
    ]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteRepositoryIntegrationTest {
    @Autowired
    private lateinit var repository: NoteRepository

    @Test
    fun `stores and loads notes by tweet partition key`() {
        repository.save(NoteEntity(NoteKey(tweetId = 99, id = 1), "BY", "first", com.example.notecontracts.NoteState.APPROVE))
        repository.save(NoteEntity(NoteKey(tweetId = 99, id = 2), "BY", "second", com.example.notecontracts.NoteState.APPROVE))

        val notes = repository.findByKeyTweetId(99)

        assertEquals(2, notes.size)
    }

    @BeforeAll
    fun setupSchema() {
        CqlSession.builder()
            .addContactPoint(InetSocketAddress(cassandra.host, cassandra.firstMappedPort))
            .withLocalDatacenter("datacenter1")
            .build().use { session ->
                session.execute(
                    SimpleStatement.builder(
                        "CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}"
                    ).setTimeout(Duration.ofSeconds(10)).build()
                )
                session.execute(
                    SimpleStatement.builder(
                        "CREATE TABLE IF NOT EXISTS distcomp.tbl_note (tweet_id bigint, id bigint, country text, content text, state text, PRIMARY KEY ((tweet_id), id))"
                    ).setTimeout(Duration.ofSeconds(10)).build()
                )
            }
    }

    companion object {
        @Container
        @JvmStatic
        private val cassandra = CassandraContainer("cassandra:4.1")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            if (!cassandra.isRunning) {
                cassandra.start()
            }
            registry.add("spring.cassandra.contact-points") { cassandra.host }
            registry.add("spring.cassandra.port") { cassandra.firstMappedPort }
            registry.add("spring.cassandra.local-datacenter") { "datacenter1" }
            registry.add("spring.cassandra.keyspace-name") { "distcomp" }
        }
    }
}
