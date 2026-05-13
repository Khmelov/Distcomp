package com.example.distcomp.security

import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.exception.BadRequestException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.repository.TweetRepository
import com.example.distcomp.service.NoteProjectionStore
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    private val currentUserService: CurrentUserService,
    private val tweetRepository: TweetRepository,
    private val noteProjectionStore: NoteProjectionStore
) {
    fun ensureAdmin() {
        if (!currentUserService.isAdmin()) {
            deny("Only ADMIN can perform this operation")
        }
    }

    fun ensureAdminOrCurrentCreator(creatorId: Long) {
        if (!currentUserService.isAdmin() && currentUserService.currentCreatorId() != creatorId) {
            deny("You can modify only your own creator profile")
        }
    }

    fun ensureCreatorRoleChangeAllowed(request: CreatorRequestTo) {
        if (!currentUserService.isAdmin() && request.role != null) {
            deny("Only ADMIN can change creator roles")
        }
    }

    fun ensureCanManageTweetRequest(request: TweetRequestTo) {
        if (currentUserService.isAdmin()) {
            return
        }
        val creatorId = request.creatorId ?: throw BadRequestException("creatorId is required")
        if (creatorId != currentUserService.currentCreatorId()) {
            deny("You can create tweets only for your own creator profile")
        }
    }

    fun ensureCanManageTweet(tweetId: Long, requestedCreatorId: Long? = null) {
        if (currentUserService.isAdmin()) {
            return
        }
        ensureOwnsTweet(tweetId)
        if (requestedCreatorId != null && requestedCreatorId != currentUserService.currentCreatorId()) {
            deny("You cannot transfer a tweet to another creator")
        }
    }

    fun ensureCanCreateNote(tweetId: Long?) {
        if (currentUserService.isAdmin()) {
            return
        }
        val targetTweetId = tweetId ?: throw BadRequestException("tweetId is required")
        ensureOwnsTweet(targetTweetId)
    }

    fun ensureCanManageNote(noteId: Long, requestedTweetId: Long? = null) {
        if (currentUserService.isAdmin()) {
            return
        }
        val route = noteProjectionStore.requireRoute(noteId)
        ensureOwnsTweet(route.tweetId)
        requestedTweetId?.let(::ensureOwnsTweet)
    }

    private fun ensureOwnsTweet(tweetId: Long) {
        val tweet = tweetRepository.findById(tweetId) ?: throw NotFoundException("Tweet with id $tweetId not found")
        if (tweet.creatorId != currentUserService.currentCreatorId()) {
            deny("You can modify only your own content and comments")
        }
    }

    private fun deny(message: String): Nothing = throw AccessDeniedException(message)
}
