package com.socialnetwork.mapper;

import com.socialnetwork.dto.request.LabelRequestTo;
import com.socialnetwork.dto.response.LabelResponseTo;
import com.socialnetwork.model.Label;
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