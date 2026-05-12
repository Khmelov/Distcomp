package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.TagRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.TagResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface TagService {
    @Validated(ValidationMarker.OnCreate.class)
    TagResponseTo create(@Valid TagRequestTo entity);

    List<TagResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    TagResponseTo update(@Valid TagRequestTo entity);

    void delete(Long id);

    TagResponseTo findTagById(Long Id);
}
