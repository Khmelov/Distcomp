package by.bsuir.publisher.services.impl;

import by.bsuir.publisher.domain.Writer;
import by.bsuir.publisher.dto.requests.WriterRequestDto;
import by.bsuir.publisher.dto.requests.converters.WriterRequestConverter;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import by.bsuir.publisher.dto.responses.converters.WriterResponseConverter;
import by.bsuir.publisher.dto.responses.converters.CollectionWriterResponseConverter;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.repositories.WriterRepository;
import by.bsuir.publisher.services.WriterService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class WriterServiceImpl implements WriterService {

    private final WriterRepository writerRepository;
    private final WriterRequestConverter writerRequestConverter;
    private final WriterResponseConverter writerResponseConverter;
    private final CollectionWriterResponseConverter collectionWriterResponseConverter;

    @Override
    @Validated
    public WriterResponseDto create(@Valid @NonNull WriterRequestDto dto) throws EntityExistsException {
        Optional<Writer> writer = dto.getId() == null ? Optional.empty() : writerRepository.findById(dto.getId());
        if (writer.isEmpty()) {
            return writerResponseConverter.toDto(writerRepository.save(writerRequestConverter.fromDto(dto)));
        } else {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<WriterResponseDto> read(@NonNull Long id) {
        return writerRepository.findById(id).flatMap(writer -> Optional.of(
                writerResponseConverter.toDto(writer)));
    }

    @Override
    @Validated
    public WriterResponseDto update(@Valid @NonNull WriterRequestDto dto) throws NoEntityExistsException {
        Optional<Writer> writer = dto.getId() == null || writerRepository.findById(dto.getId()).isEmpty() ?
                Optional.empty() : Optional.of(writerRequestConverter.fromDto(dto));
        return writerResponseConverter.toDto(writerRepository.save(writer.orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException))));
    }

    @Override
    public Long delete(@NonNull Long id) throws NoEntityExistsException {
        Optional<Writer> writer = writerRepository.findById(id);
        writerRepository.deleteById(writer.map(Writer::getId).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
        return writer.get().getId();
    }

    @Override
    public List<WriterResponseDto> readAll() {
        return collectionWriterResponseConverter.toListDto(writerRepository.findAll());
    }
}
