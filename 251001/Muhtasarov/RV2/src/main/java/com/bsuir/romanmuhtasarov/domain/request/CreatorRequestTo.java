package com.bsuir.romanmuhtasarov.domain.request;

import jakarta.validation.constraints.Size;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;

public record CreatorRequestTo(
        Long id,
        @Size(min = 3, max = 32, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
        String login,
        @Size(min = 8, max = 32, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
        String password,
        @Size(min = 3, max = 32, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
        String firstname,
        @Size(min = 3, max = 32, groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
        String lastname) {
}
