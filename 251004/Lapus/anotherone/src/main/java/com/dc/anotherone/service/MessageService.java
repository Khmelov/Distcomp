package com.dc.anotherone.service;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.model.blo.Message;
import com.dc.anotherone.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MessageService{

    @Autowired
    private MessageRepository repository;

    public Collection<Message> getAll() {
        return repository.findAll();
    }

    public Message getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ServiceException("Сообщение не найдено", 404));
    }

    public Message save(Message Message) {
        if(!validator(Message))
        {
            return null;
        }
        return repository.save(Message);
    }

    public Message update(Message Message) {
        if(!validator(Message))
        {
            return null;
        }
        return repository.save(Message);
    }

    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> new ServiceException("Нечего удалить", 404));
        repository.deleteById(id);
    }

    private boolean validator(Message Message){
        if (Message.getContent().length() < 2 || Message.getContent().length() > 2048
        ) {
            return false;
        }
        return true;
    }
}
