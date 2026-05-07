package com.distcomp.mapper

import com.distcomp.dto.marker.MarkerRequestTo
import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.entity.Marker
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface MarkerMapper {
    fun toMarkerResponse(marker: Marker) : MarkerResponseTo

    fun toMarkerEntity(markerRequestTo: MarkerRequestTo) : Marker
}