package com.example.task310.dto;
import java.io.Serializable;

public record WriterResponseTo(Long id, String login, String firstname, String lastname) implements Serializable {}