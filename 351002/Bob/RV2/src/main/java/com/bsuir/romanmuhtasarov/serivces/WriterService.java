package com.bsuir.romanmuhtasarov.serivces;

import com.bsuir.romanmuhtasarov.domain.entity.Writer;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.request.WriterRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.WriterResponseTo;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

public interface WriterService {
    @Validated(ValidationMarker.OnCreate.class)
    WriterResponseTo create(@Valid WriterRequestTo entity);

    List<WriterResponseTo> read();

    @Validated(ValidationMarker.OnUpdate.class)
    WriterResponseTo update(@Valid WriterRequestTo entity);

    void delete(Long id);

    WriterResponseTo findWriterById(Long id);

    Optional<Writer> findWriterByIdExt(Long id);
}
