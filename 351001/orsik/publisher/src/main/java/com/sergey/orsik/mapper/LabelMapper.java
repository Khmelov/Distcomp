package com.sergey.orsik.mapper;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;
import com.sergey.orsik.entity.Label;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class LabelMapper {

    public Label toEntity(LabelRequestTo request) {
        if (request == null) {
            return null;
        }
        return new Label(
                request.getId(),
                request.getName(),
                new HashSet<>()
        );
    }

    public LabelResponseTo toResponse(Label entity) {
        if (entity == null) {
            return null;
        }
        return new LabelResponseTo(
                entity.getId(),
                entity.getName()
        );
    }
}
