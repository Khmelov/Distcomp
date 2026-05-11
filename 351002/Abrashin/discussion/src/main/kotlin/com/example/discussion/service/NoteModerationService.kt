package com.example.discussion.service

import com.example.notecontracts.NoteState
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class NoteModerationService(
    @Value("\${discussion.moderation.stop-words}") stopWordsProperty: String
) {
    private val stopWords = stopWordsProperty.split(',')
        .map(String::trim)
        .filter(String::isNotBlank)
        .map(String::lowercase)
        .toSet()

    fun moderate(content: String): NoteState {
        val normalized = content.lowercase()
        return if (stopWords.any { normalized.contains(it) }) {
            NoteState.DECLINE
        } else {
            NoteState.APPROVE
        }
    }
}
