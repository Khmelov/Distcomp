package by.molchan.Publisher.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class CreatorRequestDTO {
    private Long id;

    @NotBlank(message = "Login may not be blank")
    @Size(min = 2, max = 64, message = "Login should be between 2 and 64 symbols")
    private String login;

    @NotBlank(message = "Password may not be blank")
    @Size(min = 8, max = 128, message = "Password should be between 8 and 128 symbols")
    private String password;

    @NotBlank(message = "Firstname may not be blank")
    @Size(min = 2, max = 64, message = "Firstname should be between 2 and 64 symbols")
    private String firstname;

    @NotBlank(message = "Lastname may not be blank")
    @Size(min = 2, max = 64, message = "Lastname should be between 2 and 64 symbols")
    private String lastname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
