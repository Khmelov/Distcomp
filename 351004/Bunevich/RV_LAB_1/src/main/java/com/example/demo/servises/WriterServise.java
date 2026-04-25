package com.example.demo.servises;

import com.example.demo.dto.request.WriterRequestTo;
import com.example.demo.dto.response.WriterResponseTo;
import com.example.demo.exeptionHandler.WriterNotFoundException;
import com.example.demo.mapper.WriterMapper;
import com.example.demo.memoryRepository.repozitoryImplementation.WriterInMemoryRepository;
import com.example.demo.models.Writer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WriterServise {
    public final WriterInMemoryRepository writerInMemoryRepository;
    public final WriterMapper writerMapper;
    public WriterResponseTo create(WriterRequestTo request){
        log.debug("Creating new writer: {}", request);
        Writer writer = writerMapper.requestToEntity(request);
        Writer savedWriter = writerInMemoryRepository.save(writer);
        return writerMapper.toResponse(savedWriter);
    }
    public List<WriterResponseTo> findAll(){
        log.debug("Finding all writers");
        return writerMapper.toResponseList(writerInMemoryRepository.findAll());
    }
    public void delete(Long id){
        log.debug("Deleting writer with id: {}", id);
        if(!writerInMemoryRepository.existById(id)) {
            throw new WriterNotFoundException(id);
        }
        writerInMemoryRepository.deleteById(id);
    }
    public WriterResponseTo findById(Long id){
        log.debug("Finding writer by id: {}", id);
        if(!writerInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Writer writer = writerInMemoryRepository.findById(id)
                .orElseThrow(() -> new WriterNotFoundException(id));
        return writerMapper.toResponse(writer);
    }
    public WriterResponseTo update(Long id, WriterRequestTo writerRequestTo){
        log.debug("Updating writer with id: {} with data: {}", id, writerRequestTo);
        if(!writerInMemoryRepository.existById(id)){
            throw new WriterNotFoundException(id);
        }
        Writer writer = writerMapper.requestToEntity(writerRequestTo);
        return writerMapper.toResponse(writerInMemoryRepository.update(id, writer));
    }
}
