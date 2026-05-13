package com.example.discussion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.InetAddress

@SpringBootApplication
class DiscussionApplication

fun main(args: Array<String>) {
    CassandraConnectionDefaults.apply()
    runApplication<DiscussionApplication>(*args)
}

internal object CassandraConnectionDefaults {
    private const val defaultHost = "127.0.0.1"
    private const val defaultPort = "9042"
    private const val defaultDatacenter = "datacenter1"
    private const val defaultKeyspace = "distcomp"
    private val candidateHosts = listOf(defaultHost, "localhost", "cassandra")

    fun apply(hostChecker: (String) -> Boolean = ::canResolve) {
        val port = resolveProperty(
            springProperty = "spring.cassandra.port",
            aliasProperty = "discussion.cassandra.port",
            envName = "SPRING_CASSANDRA_PORT",
            aliasEnvName = "CASSANDRA_PORT",
            defaultValue = defaultPort
        )
        val datacenter = resolveProperty(
            springProperty = "spring.cassandra.local-datacenter",
            aliasProperty = "discussion.cassandra.datacenter",
            envName = "SPRING_CASSANDRA_LOCAL_DATACENTER",
            aliasEnvName = "CASSANDRA_DATACENTER",
            defaultValue = defaultDatacenter
        )
        val keyspace = resolveProperty(
            springProperty = "spring.cassandra.keyspace-name",
            aliasProperty = "discussion.cassandra.keyspace",
            envName = "SPRING_CASSANDRA_KEYSPACE_NAME",
            aliasEnvName = "CASSANDRA_KEYSPACE",
            defaultValue = defaultKeyspace
        )
        val host = resolveHost(hostChecker)
        val liquibaseHost = host.substringBefore(',').trim()

        System.setProperty("discussion.cassandra.host", host)
        System.setProperty("discussion.cassandra.port", port)
        System.setProperty("discussion.cassandra.datacenter", datacenter)
        System.setProperty("discussion.cassandra.keyspace", keyspace)
        System.setProperty("discussion.liquibase.url", "jdbc:cassandra://$liquibaseHost:$port/$keyspace")
    }

    internal fun resolveHost(hostChecker: (String) -> Boolean = ::canResolve): String {
        val explicitHost = resolveProperty(
            springProperty = "spring.cassandra.contact-points",
            aliasProperty = "discussion.cassandra.host",
            envName = "SPRING_CASSANDRA_CONTACT_POINTS",
            aliasEnvName = "CASSANDRA_HOST",
            defaultValue = ""
        )
        if (explicitHost.isNotBlank()) {
            return explicitHost
        }
        return candidateHosts.firstOrNull(hostChecker) ?: defaultHost
    }

    private fun resolveProperty(
        springProperty: String,
        aliasProperty: String,
        envName: String,
        aliasEnvName: String,
        defaultValue: String
    ): String = sequenceOf(
        System.getProperty(springProperty),
        System.getProperty(aliasProperty),
        System.getenv(envName),
        System.getenv(aliasEnvName)
    ).firstOrNull { !it.isNullOrBlank() } ?: defaultValue

    private fun canResolve(host: String): Boolean = try {
        InetAddress.getByName(host)
        true
    } catch (_: Exception) {
        false
    }
}
