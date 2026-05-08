package com.github.Lexya06.startrestapp.discussion.impl.controller.abstraction;

import com.github.Lexya06.startrestapp.discussion.impl.config.ApiProperties;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.abstraction.BaseSearchCriteria;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.discussion.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.discussion.api.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public abstract class BaseController<T extends BaseEntity<DBKey>, DBKey, APIKey, RequestDTO,ResponseDTO, C extends BaseSearchCriteria> {
    protected abstract BaseEntityService<T, DBKey, APIKey, RequestDTO,ResponseDTO, C> getBaseService();
    @Autowired
    @Getter
    protected ApiProperties apiProperties;


    @GetMapping("/by-key/{id}")
    public ResponseEntity<ResponseDTO> get(@PathVariable APIKey id){
        return ResponseEntity.ok(getBaseService().getEntityById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createEntity(@Valid @RequestBody RequestDTO requestDTO){
        return new ResponseEntity<>(getBaseService().createEntity(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/by-key/{id}")
    public ResponseEntity<ResponseDTO> updateEntity(@PathVariable APIKey id, @Valid @RequestBody RequestDTO requestDTO){
        return ResponseEntity.ok(getBaseService().updateEntity(id, requestDTO));
    }

    @DeleteMapping("/by-key/{id}")
    public ResponseEntity<Void> deleteEntity(@PathVariable APIKey id){
        getBaseService().deleteEntityById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ResponseDTO>> getAllEntitiesByCriteria(@ModelAttribute C criteria){
        // Формируем ответ
        PagedResponse<ResponseDTO> pagedResponse = getBaseService().getAllEntitiesByCriteria(criteria);
        return ResponseEntity.ok()
                .header("X-Next-Paging-State", pagedResponse.getNextPagingState())
                .header("Access-Control-Expose-Headers", "X-Next-Paging-State")
                .body(pagedResponse.getData());
    }

}
