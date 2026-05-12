package com.example.distcomp.service

import com.example.distcomp.cache.CacheKeys
import com.example.distcomp.cache.CacheNames
import com.example.distcomp.cache.CacheSupport
import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteCommandService: NoteCommandService,
    private val projectionStore: NoteProjectionStore,
    private val cacheSupport: CacheSupport
) {
    fun create(request: NoteRequestTo): NoteResponseTo =
        noteCommandService.create(request).also {
            cacheSupport.clear(CacheNames.NOTES_PAGE)
            request.tweetId?.let { tweetId -> cacheSupport.evict(CacheNames.NOTES_BY_TWEET_ID, tweetId) }
        }

    fun getById(id: Long): NoteResponseTo =
        cacheSupport.getOrPut(CacheNames.NOTES_BY_ID, id) {
            noteCommandService.getById(id)
        }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        cacheSupport.getOrPut(CacheNames.NOTES_PAGE, CacheKeys.page(page, size, sort)) {
            noteCommandService.getAll(page, size, sort)
        }

    fun put(id: Long, request: NoteRequestTo): NoteResponseTo {
        val currentRoute = projectionStore.requireRoute(id)
        return noteCommandService.put(id, request).also { response ->
            cacheSupport.put(CacheNames.NOTES_BY_ID, id, response)
            evictNoteCollections(currentRoute.tweetId, response.tweetId)
        }
    }

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo {
        val currentRoute = projectionStore.requireRoute(id)
        return noteCommandService.patch(id, request).also { response ->
            cacheSupport.put(CacheNames.NOTES_BY_ID, id, response)
            evictNoteCollections(currentRoute.tweetId, response.tweetId)
        }
    }

    fun delete(id: Long) {
        val currentRoute = projectionStore.requireRoute(id)
        noteCommandService.delete(id)
        cacheSupport.evict(CacheNames.NOTES_BY_ID, id)
        evictNoteCollections(currentRoute.tweetId)
    }

    fun getNotesByTweetId(tweetId: Long): List<NoteResponseTo> =
        cacheSupport.getOrPut(CacheNames.NOTES_BY_TWEET_ID, tweetId) {
            noteCommandService.getByTweetId(tweetId)
        }

    private fun evictNoteCollections(vararg tweetIds: Long?) {
        cacheSupport.clear(CacheNames.NOTES_PAGE)
        tweetIds.filterNotNull().distinct().forEach { tweetId ->
            cacheSupport.evict(CacheNames.NOTES_BY_TWEET_ID, tweetId)
        }
    }
}
