package com.distcomp.service

import com.distcomp.dto.marker.MarkerRequestTo
import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.exception.MarkerNotFoundException
import com.distcomp.mapper.MarkerMapper
import com.distcomp.repository.MarkerRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkerService(
    val markerMapper: MarkerMapper,
    val markerRepository: MarkerRepository
) {
    @Transactional
    @CachePut(value = ["markers"], key = "#result.id")
    @CacheEvict(value = ["markers"], key = "'all'")
    fun createMarker(markerRequestTo: MarkerRequestTo): MarkerResponseTo {
        val marker = markerMapper.toMarkerEntity(markerRequestTo)
        markerRepository.save(marker)
        return markerMapper.toMarkerResponse(marker)
    }

    @Cacheable(value = ["markers"], key = "#id")
    fun readMarkerById(id: Long): MarkerResponseTo {
        val marker = markerRepository.findByIdOrNull(id)
            ?: throw MarkerNotFoundException("Marker with id $id not found")
        return markerMapper.toMarkerResponse(marker)
    }

    @Cacheable(value = ["markers"], key = "'all'")
    fun readAll(): List<MarkerResponseTo> {
        return markerRepository.findAll().map { markerMapper.toMarkerResponse(it) }
    }

    @Transactional
    @CachePut(value = ["markers"], key = "#markerId")
    @CacheEvict(value = ["markers"], key = "'all'")
    fun updateMarker(markerRequestTo: MarkerRequestTo, markerId: Long?): MarkerResponseTo {
        if (markerId == null || markerRepository.findByIdOrNull(markerId) == null) {
            throw MarkerNotFoundException("Marker with id $markerId not found")
        }
        val marker = markerMapper.toMarkerEntity(markerRequestTo)
        marker.id = markerId
        markerRepository.save(marker)
        return markerMapper.toMarkerResponse(marker)
    }

    @Transactional
    @Caching(evict = [
        CacheEvict(value = ["markers"], key = "#id"),
        CacheEvict(value = ["markers"], key = "'all'")
    ])
    fun removeMarkerById(id: Long) {
        if (markerRepository.findByIdOrNull(id) == null) {
            throw MarkerNotFoundException("Marker with id $id not found")
        }
        markerRepository.deleteById(id)
    }
}