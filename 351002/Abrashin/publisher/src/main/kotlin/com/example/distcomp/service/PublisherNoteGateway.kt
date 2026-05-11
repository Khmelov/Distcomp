package com.example.distcomp.service

import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteReply

interface PublisherNoteGateway {
    fun send(commandKey: String, command: NoteCommand)
    fun sendAndAwait(commandKey: String, command: NoteCommand): NoteReply
}
