package com.task310.socialnetwork.mapper;

import com.task310.socialnetwork.dto.request.LabelRequestTo;
import com.task310.socialnetwork.dto.response.LabelResponseTo;
import com.task310.socialnetwork.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public Label toEntity(LabelRequestTo request) {
        if (request == null) {
            return null;
        }

        Label label = new Label();
        label.setName(request.getName());
        return label;
    }

    public LabelResponseTo toResponse(Label entity) {
        if (entity == null) {
            return null;
        }

        LabelResponseTo response = new LabelResponseTo();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}