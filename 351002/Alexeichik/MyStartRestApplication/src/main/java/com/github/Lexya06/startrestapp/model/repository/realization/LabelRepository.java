package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class LabelRepository extends MyCrudRepository<Label> {
    HashMap<Long, Label> labelMap = new HashMap<>();
    @Override
    protected Map<Long, Label> getBaseEntityMap() {
        return labelMap;
    }
}
