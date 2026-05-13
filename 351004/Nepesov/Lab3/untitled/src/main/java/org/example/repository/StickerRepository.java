package org.example.repository;

import org.example.model.Sticker;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StickerRepository extends CassandraRepository<Sticker, Long> {
}