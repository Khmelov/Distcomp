package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Story
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<Story, Long> {
    fun findAllByWriterId(writerId: Long): List<Story>
    fun existsByMarksId(markId: Long): Boolean
}