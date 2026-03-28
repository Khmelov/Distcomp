package com.distcomp.service

import com.distcomp.dto.marker.MarkerRequestTo
import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.exception.MarkerNotFoundException
import com.distcomp.mapper.MarkerMapper
import com.distcomp.repository.MarkerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MarkerService(
    val markerMapper: MarkerMapper,
    val markerRepository: MarkerRepository
) {
    @Transactional
    fun createMarker(markerRequestTo: MarkerRequestTo): MarkerResponseTo {
        val marker = markerMapper.toMarkerEntity(markerRequestTo)
        markerRepository.save(marker)
        return markerMapper.toMarkerResponse(marker)
    }

    fun readMarkerById(id: Long): MarkerResponseTo {
        val marker = markerRepository.findByIdOrNull(id)
            ?: throw MarkerNotFoundException("Marker with id $id not found")
        return markerMapper.toMarkerResponse(marker)
    }

    fun readAll(): List<MarkerResponseTo> {
        return markerRepository.findAll().map { markerMapper.toMarkerResponse(it) }
    }

    @Transactional
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
    fun removeMarkerById(id: Long) {
        if (markerRepository.findByIdOrNull(id) == null) {
            throw MarkerNotFoundException("Marker with id $id not found")
        }
        markerRepository.deleteById(id)
    }
}