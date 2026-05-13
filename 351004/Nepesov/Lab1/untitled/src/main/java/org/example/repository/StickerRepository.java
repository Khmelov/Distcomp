package org.example.repository;

import org.example.model.Sticker;
import org.springframework.stereotype.Repository;

@Repository
public class StickerRepository extends InMemoryRepository<Sticker> {}