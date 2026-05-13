package com.adashkevich.rest.lab.repository;

import com.adashkevich.rest.lab.model.Message;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepository extends InMemoryCrudRepository<Message> {}
