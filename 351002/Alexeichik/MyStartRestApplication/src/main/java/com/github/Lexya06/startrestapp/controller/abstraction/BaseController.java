package com.github.Lexya06.startrestapp.controller.abstraction;

import com.github.Lexya06.startrestapp.config.ApiProperties;
import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @GetMapping
    public ResponseEntity<Set<ResponseDTO>> getAllEntities(){
        return ResponseEntity.ok(getBaseService().getEntities());
    }

}
