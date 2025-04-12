package org.ex.distributed_computing.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.LabelRequestDTO;
import org.ex.distributed_computing.dto.response.LabelResponseDTO;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.LabelMapper;
import org.ex.distributed_computing.model.Label;
import org.ex.distributed_computing.repository.LabelRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelService {

  private final LabelRepository labelRepository;
  private final LabelMapper labelMapper;

  public List<LabelResponseDTO> getAllLabels() {
    return labelMapper.toDtoList(labelRepository.findAll());
  }

  public LabelResponseDTO getLabelById(Long id) {
    Label label = labelRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Label not found"));
    return labelMapper.toDto(label);
  }

  public LabelResponseDTO createLabel(LabelRequestDTO requestDTO) {
    Label label = labelMapper.toEntity(requestDTO);
    labelRepository.save(label);
    return labelMapper.toDto(label);
  }

  public LabelResponseDTO updateLabel(LabelRequestDTO requestDTO) {
    Label label = labelRepository.findById(requestDTO.id())
        .orElseThrow(() -> new NotFoundException("Label not found"));

    label.setName(requestDTO.name());
    labelRepository.save(label);
    return labelMapper.toDto(label);
  }

  public void deleteLabel(Long id) {
    Label label = labelRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Label not found"));
    labelRepository.delete(label);
  }
}

