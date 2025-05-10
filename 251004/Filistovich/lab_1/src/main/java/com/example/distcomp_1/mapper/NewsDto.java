package com.example.distcomp_1.mapper;

import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsDto {
    News.Out Out(News entity);

    News In(News.In inputDto);
}