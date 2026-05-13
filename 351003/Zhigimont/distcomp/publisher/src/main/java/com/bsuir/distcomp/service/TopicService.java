package com.bsuir.distcomp.service;

import com.bsuir.distcomp.dto.TopicRequestTo;
import com.bsuir.distcomp.dto.TopicResponseTo;
import com.bsuir.distcomp.entity.Marker;
import com.bsuir.distcomp.entity.Topic;
import com.bsuir.distcomp.exception.EntityAlreadyExistsException;
import com.bsuir.distcomp.exception.EntityNotFoundException;
import com.bsuir.distcomp.mapper.TopicMapper;
import com.bsuir.distcomp.repository.MarkerRepository;
import com.bsuir.distcomp.repository.TopicRepository;
import com.bsuir.distcomp.repository.WriterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService implements CrudService<TopicRequestTo, TopicResponseTo>{

    private final TopicRepository repository;
    private final MarkerRepository markerRepository;
    private final WriterRepository writerRepository;
    private final TopicMapper mapper;

    public TopicResponseTo create(TopicRequestTo dto) {

        if(repository.findByTitle(dto.getTitle()).isPresent()){
            throw new EntityAlreadyExistsException("Topic with such title already exists");
        }

        Topic topic = mapper.toEntity(dto);

        var writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        topic.setWriter(writer);

        topic.setCreated(LocalDateTime.now());
        topic.setModified(LocalDateTime.now());

        if (dto.getMarkers() != null) {

            List<Marker> markers = dto.getMarkers().stream()
                    .map(name -> markerRepository.findByName(name)
                            .orElseGet(() -> {
                                Marker newMarker = new Marker();
                                newMarker.setName(name);
                                return markerRepository.save(newMarker);
                            }))
                    .toList();

            topic.setMarkers(markers);
        }

        Topic saved = repository.saveAndFlush(topic);

        return mapper.toDto(saved);
    }


    public List<TopicResponseTo> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public TopicResponseTo getById(Long id) {

        Topic topic = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        return mapper.toDto(topic);
    }

    public TopicResponseTo update(Long id, TopicRequestTo dto) {

        Topic existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        var writer = writerRepository.findById(dto.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        existing.setWriter(writer);
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setModified(LocalDateTime.now());

        Topic updated = repository.saveAndFlush(existing);

        return mapper.toDto(updated);
    }


    @Transactional
    public void delete(Long id) {

        repository.deleteById(id);
        repository.flush();

        markerRepository.deleteUnusedMarkers();
    }
}


