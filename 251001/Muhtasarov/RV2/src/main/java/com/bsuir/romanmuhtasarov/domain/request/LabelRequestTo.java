package com.bsuir.romanmuhtasarov.domain.request;

import jakarta.validation.constraints.Size;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;

public record LabelRequestTo(
        Long id,
        @Size(min = 3, max = 32, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
        String name) {
}
