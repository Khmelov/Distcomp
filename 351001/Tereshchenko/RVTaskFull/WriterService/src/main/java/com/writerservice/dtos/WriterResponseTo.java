package com.writerservice.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriterResponseTo {

    public Long id;

    public String login;

    public String firstname;

    public String lastname;
}
