package com.example.entitiesapp.service

import com.example.entitiesapp.dto.CommentState
import org.springframework.stereotype.Service

@Service
class ModerationService {
    private val forbiddenWords = listOf("спам", "реклама", "badword")

    fun moderate(content: String): CommentState {
        return if (forbiddenWords.any { content.contains(it, ignoreCase = true) }) {
            CommentState.DECLINE
        } else {
            CommentState.APPROVE
        }
    }
}