package com.example.distcomp.controller

import com.example.distcomp.data.datasource.creator.local.CreatorJpaRepository
import com.example.distcomp.data.datasource.note.local.NoteJpaRepository
import com.example.distcomp.data.datasource.sticker.local.StickerJpaRepository
import com.example.distcomp.data.datasource.tweet.local.TweetJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseControllerTest {

    @Autowired
    protected lateinit var creatorRepository: CreatorJpaRepository
    @Autowired
    protected lateinit var tweetRepository: TweetJpaRepository
    @Autowired
    protected lateinit var stickerRepository: StickerJpaRepository
    @Autowired
    protected lateinit var noteRepository: NoteJpaRepository

    @BeforeEach
    fun clearDatabase() {
        noteRepository.deleteAll()
        tweetRepository.deleteAll()
        stickerRepository.deleteAll()
        creatorRepository.deleteAll()
    }

    companion object {
        private val postgres = PostgreSQLContainer("postgres:15-alpine").apply {
            withDatabaseName("postgres")
            withUsername("postgres")
            withPassword("postgres")
            withInitScript("init.sql")
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl + "?currentSchema=distcomp" }
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.liquibase.enabled") { "true" }
            registry.add("spring.liquibase.default-schema") { "distcomp" }
        }
    }
}
