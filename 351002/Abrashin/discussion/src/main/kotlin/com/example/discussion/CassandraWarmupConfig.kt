package com.example.discussion

import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "discussion.cassandra.warmup",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class CassandraWarmup(
    private val session: CqlSession,
    @Value("\${spring.cassandra.keyspace-name}") private val keyspace: String
) {
    @EventListener(ApplicationReadyEvent::class)
    fun warmup() {
        val statements = listOf(
            "SELECT release_version FROM system.local",
            "SELECT * FROM $keyspace.tbl_note LIMIT 1",
            "SELECT * FROM $keyspace.tbl_note WHERE tweet_id = ?",
            "SELECT * FROM $keyspace.tbl_note WHERE id = ? ALLOW FILTERING",
            "INSERT INTO $keyspace.tbl_note (tweet_id, id, country, content) VALUES (?, ?, ?, ?)",
            "DELETE FROM $keyspace.tbl_note WHERE tweet_id = ? AND id = ?"
        )

        statements.forEach { statement ->
            session.prepare(statement)
        }
    }
}
