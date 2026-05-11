package com.example.discussion

import kotlin.test.Test
import kotlin.test.assertEquals

class CassandraConnectionDefaultsTest {
    @Test
    fun `resolveHost keeps explicitly configured host`() {
        val key = "discussion.cassandra.host"
        val previous = System.getProperty(key)
        try {
            System.setProperty(key, "db.internal")

            val host = CassandraConnectionDefaults.resolveHost { false }

            assertEquals("db.internal", host)
        } finally {
            restoreProperty(key, previous)
        }
    }

    @Test
    fun `resolveHost falls back to container hostname when localhost is unavailable`() {
        val key = "discussion.cassandra.host"
        val previous = System.getProperty(key)
        try {
            System.clearProperty(key)

            val host = CassandraConnectionDefaults.resolveHost { candidate -> candidate == "cassandra" }

            assertEquals("cassandra", host)
        } finally {
            restoreProperty(key, previous)
        }
    }

    @Test
    fun `resolveHost defaults to localhost when no candidate is reachable`() {
        val key = "discussion.cassandra.host"
        val previous = System.getProperty(key)
        try {
            System.clearProperty(key)

            val host = CassandraConnectionDefaults.resolveHost { false }

            assertEquals("127.0.0.1", host)
        } finally {
            restoreProperty(key, previous)
        }
    }

    private fun restoreProperty(name: String, value: String?) {
        if (value == null) {
            System.clearProperty(name)
        } else {
            System.setProperty(name, value)
        }
    }
}
