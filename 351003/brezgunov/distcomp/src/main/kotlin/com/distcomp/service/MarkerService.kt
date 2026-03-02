package com.distcomp.service

import com.distcomp.dto.marker.MarkerRequestTo
import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.entity.Marker
import com.distcomp.exception.MarkerNotFoundException
import com.distcomp.mapper.MarkerMapper
import com.distcomp.repository.CrudRepository
import org.springframework.stereotype.Service

@Service
class MarkerService(
    val markerMapper: MarkerMapper,
    val markerRepository: CrudRepository<Marker>
) {
    fun createMarker(markerRequestTo: MarkerRequestTo): MarkerResponseTo {
        val marker = markerMapper.toMarkerEntity(markerRequestTo)
        markerRepository.save(marker)
        return markerMapper.toMarkerResponse(marker)
    }

    fun readMarkerById(id: Long): MarkerResponseTo {
        val marker = markerRepository.findById(id)
            ?: throw MarkerNotFoundException("Marker with id $id not found")
        return markerMapper.toMarkerResponse(marker)
    }

    fun readAll(): List<MarkerResponseTo> {
        return markerRepository.findAll().map { markerMapper.toMarkerResponse(it) }
    }

    fun updateMarker(markerRequestTo: MarkerRequestTo, markerId: Long?): MarkerResponseTo {
        if (markerId == null || markerRepository.findById(markerId) == null) {
            throw MarkerNotFoundException("Marker with id $markerId not found")
        }

        val marker = markerMapper.toMarkerEntity(markerRequestTo)
        marker.id = markerId

        markerRepository.save(marker)
        return markerMapper.toMarkerResponse(marker)
    }

    fun removeMarkerById(id: Long) {
        if (markerRepository.findById(id) == null) {
            throw MarkerNotFoundException("Marker with id $id not found")
        }
        markerRepository.removeById(id)
    }
}