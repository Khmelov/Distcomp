package com.dc.anotherone.mapper;

import com.dc.anotherone.model.blo.Marker;
import com.dc.anotherone.model.dto.MarkerRequestTo;
import com.dc.anotherone.model.dto.MarkerResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MarkerMapper {

    Marker mapToBlo(MarkerRequestTo MarkerRequestTo);

    MarkerResponseTo mapToDto(Marker Marker);

    List<Marker> mapToListBlo(List<MarkerRequestTo> kList);

    List<MarkerResponseTo> mapToListDto(List<Marker> tList);

}
