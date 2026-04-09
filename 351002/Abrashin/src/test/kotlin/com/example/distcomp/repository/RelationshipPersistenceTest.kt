package com.example.distcomp.repository

import com.example.distcomp.data.dbo.*
import com.example.distcomp.data.datasource.creator.local.CreatorJpaRepository
import com.example.distcomp.data.datasource.note.local.NoteJpaRepository
import com.example.distcomp.data.datasource.sticker.local.StickerJpaRepository
import com.example.distcomp.data.datasource.tweet.local.TweetJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class RelationshipPersistenceTest : PersistenceTest() {

    @Autowired
    private lateinit var creatorJpaRepository: CreatorJpaRepository
    @Autowired
    private lateinit var tweetJpaRepository: TweetJpaRepository
    @Autowired
    private lateinit var stickerJpaRepository: StickerJpaRepository
    @Autowired
    private lateinit var noteJpaRepository: NoteJpaRepository

    @Test
    fun `GIVEN a creator and a tweet WHEN saving tweet THEN relationship should be persisted`() {
        // Given
        val creator = creatorJpaRepository.save(CreatorDbo().apply {
            login = "c1"; password = "p"; firstname = "f"; lastname = "l"
        })
        val tweet = TweetDbo().apply {
            this.creator = creator
            title = "Title"
            content = "Content"
            created = LocalDateTime.now()
            modified = LocalDateTime.now()
        }

        // When
        val savedTweet = tweetJpaRepository.save(tweet)

        // Then
        val fetchedTweet = tweetJpaRepository.findById(savedTweet.id!!).get()
        assertEquals(creator.id, fetchedTweet.creator?.id)
    }

    @Test
    fun `GIVEN a tweet and stickers WHEN saving tweet THEN many-to-many relationship should be persisted`() {
        // Given
        val creator = creatorJpaRepository.save(CreatorDbo().apply {
            login = "c2"; password = "p"; firstname = "f"; lastname = "l"
        })
        val s1 = stickerJpaRepository.save(StickerDbo().apply { name = "Happy" })
        val s2 = stickerJpaRepository.save(StickerDbo().apply { name = "Cool" })
        
        val tweet = TweetDbo().apply {
            this.creator = creator
            title = "Title 2"
            content = "Content 2"
            created = LocalDateTime.now()
            modified = LocalDateTime.now()
            stickers.add(s1)
            stickers.add(s2)
        }

        // When
        val savedTweet = tweetJpaRepository.save(tweet)

        // Then
        val fetchedTweet = tweetJpaRepository.findById(savedTweet.id!!).get()
        assertEquals(2, fetchedTweet.stickers.size)
        assertTrue(fetchedTweet.stickers.map { it.name }.containsAll(listOf("Happy", "Cool")))
    }

    @Test
    fun `GIVEN a tweet and a note WHEN saving note THEN relationship should be persisted`() {
        // Given
        val creator = creatorJpaRepository.save(CreatorDbo().apply {
            login = "c3"; password = "p"; firstname = "f"; lastname = "l"
        })
        val tweet = tweetJpaRepository.save(TweetDbo().apply {
            this.creator = creator
            title = "Title 3"
            content = "Content 3"
            created = LocalDateTime.now()
            modified = LocalDateTime.now()
        })
        val note = NoteDbo().apply {
            this.tweet = tweet
            content = "Note content"
        }

        // When
        val savedNote = noteJpaRepository.save(note)

        // Then
        val fetchedNote = noteJpaRepository.findById(savedNote.id!!).get()
        assertEquals(tweet.id, fetchedNote.tweet?.id)
    }
}
