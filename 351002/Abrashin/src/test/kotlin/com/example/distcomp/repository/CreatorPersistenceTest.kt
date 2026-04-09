package com.example.distcomp.repository

import com.example.distcomp.data.dbo.CreatorDbo
import com.example.distcomp.data.datasource.creator.local.CreatorJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreatorPersistenceTest : PersistenceTest() {

    @Autowired
    private lateinit var creatorJpaRepository: CreatorJpaRepository

    @Test
    fun `GIVEN a new creator WHEN saving THEN it should be persisted with generated id`() {
        // Given
        val creator = CreatorDbo().apply {
            login = "test@example.com"
            password = "password123"
            firstname = "Test"
            lastname = "User"
        }

        // When
        val saved = creatorJpaRepository.save(creator)

        // Then
        assertNotNull(saved.id)
        val fetched = creatorJpaRepository.findById(saved.id!!).orElse(null)
        assertNotNull(fetched)
        assertEquals("test@example.com", fetched.login)
    }

    @Test
    fun `GIVEN an existing creator login WHEN finding by login THEN it should return the creator`() {
        // Given
        val creator = CreatorDbo().apply {
            login = "test2@example.com"
            password = "password123"
            firstname = "Test2"
            lastname = "User2"
        }
        creatorJpaRepository.save(creator)

        // When
        val fetched = creatorJpaRepository.findByLogin("test2@example.com").orElse(null)

        // Then
        assertNotNull(fetched)
        assertEquals("test2@example.com", fetched.login)
    }
}
