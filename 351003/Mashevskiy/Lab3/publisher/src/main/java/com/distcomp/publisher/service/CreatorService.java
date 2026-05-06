package com.distcomp.publisher.service;

import com.distcomp.publisher.dto.CreatorRequestDTO;
import com.distcomp.publisher.dto.CreatorResponseDTO;
import com.distcomp.publisher.model.Creator;
import com.distcomp.publisher.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreatorService {

    @Autowired
    private CreatorRepository creatorRepository;

    public List<CreatorResponseDTO> getAll() {
        return creatorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CreatorResponseDTO getById(Long id) {
        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        return toResponse(creator);
    }

    public CreatorResponseDTO create(CreatorRequestDTO dto) {
        Creator creator = new Creator();
        creator.setLogin(dto.getLogin());
        creator.setPassword(dto.getPassword());
        creator.setFirstname(dto.getFirstname());
        creator.setLastname(dto.getLastname());
        return toResponse(creatorRepository.save(creator));
    }

    public CreatorResponseDTO update(Long id, CreatorRequestDTO dto) {
        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        creator.setLogin(dto.getLogin());
        creator.setPassword(dto.getPassword());
        creator.setFirstname(dto.getFirstname());
        creator.setLastname(dto.getLastname());
        return toResponse(creatorRepository.save(creator));
    }

    public void delete(Long id) {
        creatorRepository.deleteById(id);
    }

    private CreatorResponseDTO toResponse(Creator creator) {
        return new CreatorResponseDTO(creator.getId(), creator.getLogin(),
                creator.getFirstname(), creator.getLastname());
    }
}