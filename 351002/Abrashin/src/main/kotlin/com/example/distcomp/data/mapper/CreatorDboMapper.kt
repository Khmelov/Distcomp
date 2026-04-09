package com.example.distcomp.data.mapper

import com.example.distcomp.data.dbo.CreatorDbo
import com.example.distcomp.model.Creator
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface CreatorDboMapper {
    fun toDbo(model: Creator): CreatorDbo
    fun toModel(dbo: CreatorDbo): Creator
}
