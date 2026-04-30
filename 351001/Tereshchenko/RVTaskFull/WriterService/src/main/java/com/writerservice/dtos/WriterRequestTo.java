package com.writerservice.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.writerservice.models.WriterRole;

@Data
public class WriterRequestTo {

    @Size(min = 2, max = 64)
    public String login;

    @Size(min = 8, max = 128)
    public String password;

    @Size(min = 2, max = 64)
    public String firstname;

    @Size(min = 2, max = 64)
    public String lastname;

    public WriterRole role;

    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }

    public void setLastName(String lastName) {
        this.lastname = lastName;
    }
}
