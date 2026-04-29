package com.example.demo.exeptionHandler;

public class WriterNotFoundException extends RuntimeException{
    public WriterNotFoundException(String message) {
        super(message);
    }

    public WriterNotFoundException(Long id) {
        super("Writer with id " + id + " not found");
    }
}
