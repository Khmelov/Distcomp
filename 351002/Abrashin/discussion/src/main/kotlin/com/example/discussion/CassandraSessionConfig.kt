package com.example.discussion

import com.datastax.oss.driver.api.core.CqlIdentifier
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class CassandraSessionConfig(
    @Value("\${spring.cassandra.contact-points}") private val contactPoints: String,
    @Value("\${spring.cassandra.port}") private val defaultPort: Int,
    @Value("\${spring.cassandra.local-datacenter}") private val localDatacenter: String,
    @Value("\${spring.cassandra.keyspace-name}") private val keyspace: String
) {
    @Bean(name = ["cassandraSession"])
    fun cassandraSession(): CqlSession {
        val nodes = parseContactPoints(contactPoints, defaultPort)

        CqlSession.builder()
            .addContactPoints(nodes)
            .withLocalDatacenter(localDatacenter)
            .build()
            .use { bootstrapSession ->
                bootstrapSession.execute(
                    SimpleStatement.builder(
                        "CREATE KEYSPACE IF NOT EXISTS $keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}"
                    ).setTimeout(Duration.ofSeconds(15)).build()
                )
                bootstrapSession.execute(
                    SimpleStatement.builder(
                        "CREATE TABLE IF NOT EXISTS $keyspace.tbl_note (" +
                            "tweet_id bigint, " +
                            "id bigint, " +
                            "country text, " +
                            "content text, " +
                            "PRIMARY KEY ((tweet_id), id))"
                    ).setTimeout(Duration.ofSeconds(15)).build()
                )
            }

        return CqlSession.builder()
            .addContactPoints(nodes)
            .withLocalDatacenter(localDatacenter)
            .withKeyspace(CqlIdentifier.fromCql(keyspace))
            .build()
    }

    private fun parseContactPoints(rawContactPoints: String, fallbackPort: Int): List<InetSocketAddress> =
        rawContactPoints.split(',')
            .map(String::trim)
            .filter(String::isNotBlank)
            .map { entry ->
                val separator = entry.lastIndexOf(':')
                if (separator > 0 && separator < entry.length - 1) {
                    InetSocketAddress(entry.substring(0, separator), entry.substring(separator + 1).toInt())
                } else {
                    InetSocketAddress(entry, fallbackPort)
                }
            }
}
