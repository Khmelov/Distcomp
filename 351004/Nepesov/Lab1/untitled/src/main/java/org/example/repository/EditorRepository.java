package org.example.repository;

import org.example.model.Editor;
import org.springframework.stereotype.Repository;

@Repository
public class EditorRepository extends InMemoryRepository<Editor> {}