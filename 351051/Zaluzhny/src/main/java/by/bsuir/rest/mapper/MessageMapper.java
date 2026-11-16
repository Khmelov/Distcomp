package by.bsuir.rest.mapper;

import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    Message toEntity(MessageRequestTo request);

    MessageResponseTo toResponse(Message message);

    @Mapping(target = "id", ignore = true)
    void update(MessageRequestTo request, @MappingTarget Message message);
}
