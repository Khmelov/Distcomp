package com.example.discussion.service

import com.example.discussion.dto.request.NoteRequestTo
import com.example.discussion.exception.BadRequestException
import com.example.discussion.exception.GlobalExceptionHandler
import com.example.discussion.exception.NotFoundException
import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NoteReply
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NoteCommandConsumer(
    private val noteService: NoteService,
    private val replyProducer: NoteReplyProducer
) {
    @KafkaListener(
        topics = [NoteKafkaTopics.IN_TOPIC],
        containerFactory = "noteCommandKafkaListenerContainerFactory"
    )
    fun onCommand(command: NoteCommand) {
        val key = command.tweetId?.toString() ?: "notes-all"
        val reply = try {
            when (command.operation) {
                NoteOperation.CREATE -> successReply(command, 201, noteService.create(command.toRequest(), command.noteId))
                NoteOperation.GET_BY_ID -> successReply(
                    command,
                    200,
                    noteService.getByIdEntity(command.noteId ?: throw BadRequestException("noteId is required"))
                )
                NoteOperation.GET_ALL -> NoteReply(
                    correlationId = command.correlationId,
                    operation = command.operation,
                    success = true,
                    httpStatus = 200,
                    notes = noteService.getAllEntities(command.page ?: 0, command.size ?: 10, command.sort.toTypedArray())
                        .map(replyProducer::toPayload)
                )
                NoteOperation.GET_BY_TWEET_ID -> NoteReply(
                    correlationId = command.correlationId,
                    operation = command.operation,
                    tweetId = command.tweetId ?: throw BadRequestException("tweetId is required"),
                    success = true,
                    httpStatus = 200,
                    notes = noteService.getByTweetIdEntities(command.tweetId ?: throw BadRequestException("tweetId is required"))
                        .map(replyProducer::toPayload)
                )
                NoteOperation.PUT -> successReply(
                    command,
                    200,
                    noteService.put(command.noteId ?: throw BadRequestException("noteId is required"), command.toRequest())
                )
                NoteOperation.PATCH -> successReply(
                    command,
                    200,
                    noteService.patch(command.noteId ?: throw BadRequestException("noteId is required"), command.toRequest())
                )
                NoteOperation.DELETE -> successReply(
                    command,
                    204,
                    noteService.delete(command.noteId ?: throw BadRequestException("noteId is required"))
                )
            }
        } catch (ex: NotFoundException) {
            failureReply(command, 404, GlobalExceptionHandler.ERR_NOT_FOUND, ex.message)
        } catch (ex: BadRequestException) {
            failureReply(command, 400, GlobalExceptionHandler.ERR_BAD_REQUEST, ex.message)
        } catch (ex: Exception) {
            failureReply(command, 500, GlobalExceptionHandler.ERR_INTERNAL, ex.message ?: "Internal Error")
        }

        replyProducer.send(key, reply)
    }

    private fun successReply(command: NoteCommand, status: Int, note: com.example.discussion.model.NoteEntity): NoteReply =
        NoteReply(
            correlationId = command.correlationId,
            operation = command.operation,
            noteId = note.key.id,
            tweetId = note.key.tweetId,
            success = true,
            httpStatus = status,
            note = replyProducer.toPayload(note)
        )

    private fun failureReply(command: NoteCommand, status: Int, errorCode: Int, message: String?): NoteReply =
        NoteReply(
            correlationId = command.correlationId,
            operation = command.operation,
            noteId = command.noteId,
            tweetId = command.tweetId,
            success = false,
            httpStatus = status,
            errorCode = errorCode,
            message = message
        )

    private fun NoteCommand.toRequest(): NoteRequestTo = NoteRequestTo(
        tweetId = tweetId,
        country = country,
        content = content
    )
}
