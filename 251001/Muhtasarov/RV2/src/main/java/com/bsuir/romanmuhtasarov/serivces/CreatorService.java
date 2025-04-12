package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.Creator;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.CreatorRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CreatorResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

public interface CreatorService {
    @Validated(ValidationMarker.OnCreate.class)
    CreatorResponseTo create(@Valid CreatorRequestTo entity);

    List<CreatorResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    CreatorResponseTo update(@Valid CreatorRequestTo entity);

    void delete(Long id);

    CreatorResponseTo findCreatorById(Long id);

    Optional<Creator> findCreatorByIdExt(Long id);
}
