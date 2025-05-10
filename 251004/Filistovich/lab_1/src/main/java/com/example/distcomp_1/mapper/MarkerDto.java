package com.example.distcomp_1.mapper;

import com.example.distcomp_1.mdoel.Marker;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarkerDto {
    Marker.Out Out(Marker entity);

    Marker In(Marker.In inputDto);
}
