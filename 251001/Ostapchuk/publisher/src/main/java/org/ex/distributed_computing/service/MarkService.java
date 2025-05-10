package org.ex.distributed_computing.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.MarkRequestDTO;
import org.ex.distributed_computing.dto.response.MarkResponseDTO;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.MarkMapper;
import org.ex.distributed_computing.model.Mark;
import org.ex.distributed_computing.repository.MarkRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkService {

  private final MarkRepository markRepository;
  private final MarkMapper markMapper;

  public List<MarkResponseDTO> getAllMarks() {
    return markMapper.toDtoList(markRepository.findAll());
  }

  public MarkResponseDTO getMarkById(Long id) {
    Mark mark = markRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Mark not found"));
    return markMapper.toDto(mark);
  }

  public MarkResponseDTO createMark(MarkRequestDTO requestDTO) {
    Mark mark = markMapper.toEntity(requestDTO);
    markRepository.save(mark);
    return markMapper.toDto(mark);
  }

  public MarkResponseDTO updateMark(MarkRequestDTO requestDTO) {
    Mark mark = markRepository.findById(requestDTO.id())
        .orElseThrow(() -> new NotFoundException("Mark not found"));

    mark.setName(requestDTO.name());
    markRepository.save(mark);
    return markMapper.toDto(mark);
  }

  public void deleteMark(Long id) {
    Mark mark = markRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Mark not found"));
    markRepository.delete(mark);
  }
}

