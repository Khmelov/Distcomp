package com.distcomp.controller

import com.distcomp.dto.marker.MarkerRequestTo
import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.service.MarkerService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/{version}/markers")
class MarkerController(
    private val markerService: MarkerService
) {
    @PostMapping(version = "1.0")
    @ResponseStatus(HttpStatus.CREATED)
    fun createMarker(@RequestBody markerRequestTo: MarkerRequestTo): MarkerResponseTo {
        return markerService.createMarker(markerRequestTo)
    }

    @GetMapping("{id}")
    fun readMarkerById(@PathVariable("id") id: Long): MarkerResponseTo {
        return markerService.readMarkerById(id)
    }

    @GetMapping
    fun findAll(): List<MarkerResponseTo> {
        return markerService.readAll()
    }

    @PutMapping(path = ["/{id}", ""], version = "1.0")
    @ResponseStatus(HttpStatus.OK)
    fun updateMarker(
        @RequestBody markerRequestTo: MarkerRequestTo,
        @PathVariable("id") id: Long?
    ): MarkerResponseTo {
        return markerService.updateMarker(markerRequestTo, id)
    }

    @DeleteMapping(path = ["/{id}"], version = "1.0")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMarker(@PathVariable("id") id: Long) {
        markerService.removeMarkerById(id)
    }
}