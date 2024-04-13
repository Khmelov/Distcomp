package com.example.rv.impl.creator;

import com.example.rv.api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatorService {

    public final CrudRepository<Creator, Long> creatorCrudRepository;

    public final CreatorMapperImpl creatorMapper;
}
