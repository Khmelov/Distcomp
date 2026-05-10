package com.github.Lexya06.startrestapp.publisher.impl.controller.abstraction;

import com.github.Lexya06.startrestapp.publisher.impl.config.ApiProperties;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<T extends BaseEntity, RequestDTO,ResponseDTO> {
    protected abstract BaseEntityService<T,RequestDTO,ResponseDTO> getBaseService();
    @Autowired
    @Getter
    protected ApiProperties apiProperties;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> get(@PathVariable Long id){
        return ResponseEntity.ok(getBaseService().getEntityById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createEntity(@Valid @RequestBody RequestDTO requestDTO){
        return new ResponseEntity<>(getBaseService().createEntity(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateEntity(@PathVariable Long id, @Valid @RequestBody RequestDTO requestDTO){
        return ResponseEntity.ok(getBaseService().updateEntity(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable Long id){
        getBaseService().deleteEntityById(id);
        return ResponseEntity.noContent().build();
    }


    protected ResponseEntity<List<ResponseDTO>> getAllEntitiesBase(Predicate predicate, Pageable pageable){
        return ResponseEntity.ok(getBaseService().getEntities(predicate, pageable));
    }

    @GetMapping
    public abstract ResponseEntity<List<ResponseDTO>> getAllEntities(Predicate predicate, Pageable pageable);

}
