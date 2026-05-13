package com.adashkevich.rest.lab.repository;

import com.adashkevich.rest.lab.model.Editor;
import org.springframework.stereotype.Repository;

@Repository
public class EditorRepository extends InMemoryCrudRepository<Editor> {}
