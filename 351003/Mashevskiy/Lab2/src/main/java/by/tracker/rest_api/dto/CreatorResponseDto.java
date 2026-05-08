package by.tracker.rest_api.dto;

public class CreatorResponseDto {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private String created;
    private String modified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getCreated() { return created; }
    public void setCreated(String created) { this.created = created; }
    public String getModified() { return modified; }
    public void setModified(String modified) { this.modified = modified; }
}