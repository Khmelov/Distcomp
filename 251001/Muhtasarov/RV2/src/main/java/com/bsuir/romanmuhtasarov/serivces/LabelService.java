package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.LabelRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.LabelResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public interface LabelService {
    @Validated(ValidationMarker.OnCreate.class)
    LabelResponseTo create(@Valid LabelRequestTo entity);

    List<LabelResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    LabelResponseTo update(@Valid LabelRequestTo entity);

    void delete(Long id);

    LabelResponseTo findLabelById(Long Id);
}
