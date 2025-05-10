package com.example.distcomp_1.mapper;

import com.example.distcomp_1.mdoel.Marker;
import com.example.distcomp_1.mdoel.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageDto {
    Message.Out Out(Message entity);

    Message In(Message.In inputDto);
}
