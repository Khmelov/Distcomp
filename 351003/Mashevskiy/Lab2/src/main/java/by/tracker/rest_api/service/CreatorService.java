package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.CreatorRequestDto;
import by.tracker.rest_api.dto.CreatorResponseDto;
import by.tracker.rest_api.entity.Creator;
import by.tracker.rest_api.exception.DuplicateResourceException;
import by.tracker.rest_api.exception.ResourceNotFoundException;
import by.tracker.rest_api.exception.ValidationException;
import by.tracker.rest_api.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreatorService {

    @Autowired
    private CreatorRepository creatorRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional(readOnly = true)
    public List<CreatorResponseDto> getAll() {
        return creatorRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CreatorResponseDto getById(Long id) {
        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found", 40401));
        return toResponseDto(creator);
    }

    public CreatorResponseDto create(CreatorRequestDto dto) {
        validateCreator(dto);

        if (creatorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateResourceException("Creator with this login already exists", 40301);
        }

        Creator creator = new Creator();
        creator.setLogin(dto.getLogin());
        creator.setPassword(dto.getPassword());
        creator.setFirstname(dto.getFirstname());
        creator.setLastname(dto.getLastname());

        creator = creatorRepository.save(creator);
        return toResponseDto(creator);
    }

    public CreatorResponseDto update(CreatorRequestDto dto) {
        if (dto.getId() == null) {
            throw new ValidationException("ID is required for update", 40001);
        }

        Creator existingCreator = creatorRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found", 40401));

        if (dto.getLogin() != null && !dto.getLogin().equals(existingCreator.getLogin())) {
            if (creatorRepository.existsByLogin(dto.getLogin())) {
                throw new DuplicateResourceException("Creator with this login already exists", 40301);
            }
            existingCreator.setLogin(dto.getLogin());
        }
        if (dto.getPassword() != null) {
            if (dto.getPassword().length() < 8 || dto.getPassword().length() > 128) {
                throw new ValidationException("Password must be between 8 and 128 characters", 40002);
            }
            existingCreator.setPassword(dto.getPassword());
        }
        if (dto.getFirstname() != null) {
            if (dto.getFirstname().length() < 2 || dto.getFirstname().length() > 64) {
                throw new ValidationException("Firstname must be between 2 and 64 characters", 40003);
            }
            existingCreator.setFirstname(dto.getFirstname());
        }
        if (dto.getLastname() != null) {
            if (dto.getLastname().length() < 2 || dto.getLastname().length() > 64) {
                throw new ValidationException("Lastname must be between 2 and 64 characters", 40004);
            }
            existingCreator.setLastname(dto.getLastname());
        }

        existingCreator = creatorRepository.save(existingCreator);
        return toResponseDto(existingCreator);
    }

    public void delete(Long id) {
        if (!creatorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Creator not found", 40401);
        }
        creatorRepository.deleteById(id);
    }

    private void validateCreator(CreatorRequestDto dto) {
        if (dto.getLogin() == null || dto.getLogin().length() < 2 || dto.getLogin().length() > 64) {
            throw new ValidationException("Login must be between 2 and 64 characters", 40001);
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 8 || dto.getPassword().length() > 128) {
            throw new ValidationException("Password must be between 8 and 128 characters", 40002);
        }
        if (dto.getFirstname() == null || dto.getFirstname().length() < 2 || dto.getFirstname().length() > 64) {
            throw new ValidationException("Firstname must be between 2 and 64 characters", 40003);
        }
        if (dto.getLastname() == null || dto.getLastname().length() < 2 || dto.getLastname().length() > 64) {
            throw new ValidationException("Lastname must be between 2 and 64 characters", 40004);
        }
    }

    private CreatorResponseDto toResponseDto(Creator entity) {
        CreatorResponseDto dto = new CreatorResponseDto();
        dto.setId(entity.getId());
        dto.setLogin(entity.getLogin());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        if (entity.getCreated() != null) {
            dto.setCreated(entity.getCreated().format(formatter));
        }
        if (entity.getModified() != null) {
            dto.setModified(entity.getModified().format(formatter));
        }
        return dto;
    }
}