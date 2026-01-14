package com.socialnetwork.mapper;

import com.socialnetwork.dto.external.MessageRequestDto;
import com.socialnetwork.dto.external.MessageResponseDto;
import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseTo toResponse(MessageResponseDto externalResponse) {
        if (externalResponse == null) {
            return null;
        }

        MessageResponseTo response = new MessageResponseTo();
        response.setId(externalResponse.getId());
        response.setTweetId(externalResponse.getTweetId());
        response.setContent(externalResponse.getContent());
        return response;
    }

    public MessageRequestDto toExternalRequest(MessageRequestTo internalRequest) {
        if (internalRequest == null) {
            return null;
        }

        MessageRequestDto externalRequest = new MessageRequestDto();
        externalRequest.setCountry(internalRequest.getCountry());
        externalRequest.setTweetId(internalRequest.getTweetId());
        externalRequest.setContent(internalRequest.getContent());
        return externalRequest;
    }
}