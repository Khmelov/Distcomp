package com.group310971.gormash.repository;

import com.group310971.gormash.model.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
}
