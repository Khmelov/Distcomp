package by.bsuir.publisher.services.impl;

import by.bsuir.publisher.domain.Tag;
import by.bsuir.publisher.dto.requests.TagRequestDto;
import by.bsuir.publisher.dto.requests.converters.TagRequestConverter;
import by.bsuir.publisher.dto.responses.TagResponseDto;
import by.bsuir.publisher.dto.responses.converters.CollectionTagResponseConverter;
import by.bsuir.publisher.dto.responses.converters.TagResponseConverter;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.repositories.TagRepository;
import by.bsuir.publisher.services.TagService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(rollbackFor = {EntityExistsException.class, NoEntityExistsException.class})
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagRequestConverter tagRequestConverter;
    private final TagResponseConverter tagResponseConverter;
    private final CollectionTagResponseConverter collectionTagResponseConverter;

    @Override
    @Validated
    public TagResponseDto create(@Valid @NonNull TagRequestDto dto) throws EntityExistsException {
        Optional<Tag> tag = dto.getId() == null ? Optional.empty() : tagRepository.findById(dto.getId());
        if (tag.isEmpty()) {
            return tagResponseConverter.toDto(tagRepository.save(tagRequestConverter.fromDto(dto)));
        } else {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<TagResponseDto> read(@NonNull Long id) {
        return tagRepository.findById(id).flatMap(tag -> Optional.of(
                tagResponseConverter.toDto(tag)));
    }

    @Override
    @Validated
    public TagResponseDto update(@Valid @NonNull TagRequestDto dto) throws NoEntityExistsException {
        Optional<Tag> tag = dto.getId() == null || tagRepository.findById(dto.getId()).isEmpty() ?
                Optional.empty() : Optional.of(tagRequestConverter.fromDto(dto));
        return tagResponseConverter.toDto(tagRepository.save(tag.orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException))));
    }

    @Override
    public Long delete(@NonNull Long id) throws NoEntityExistsException {
        Optional<Tag> tag = tagRepository.findById(id);
        tagRepository.deleteById(tag.map(Tag::getId).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
        return tag.get().getId();
    }

    @Override
    public List<TagResponseDto> readAll() {
        return collectionTagResponseConverter.toListDto(tagRepository.findAll());
    }
}
