package com.example.Labs.repository;
import com.example.Labs.entity.Message;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends BaseRepository<Message, Long> {}