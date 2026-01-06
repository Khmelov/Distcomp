package com.labs.domain.repository;

import com.labs.domain.entity.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends BaseRepository<Message, Long> {
}

