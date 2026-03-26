package com.example.news.mapper;

import com.example.common.dto.MarkerRequestTo;
import com.example.common.dto.MarkerResponseTo;
import com.example.news.entity.Marker;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface MarkerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "articles", ignore = true)
    Marker toEntity(MarkerRequestTo request);

    MarkerResponseTo toResponse(Marker entity);
}