package by.bsuir.distcomp.dto.request;

public class LoginRequestTo {
    private String login;
    private String password;

    public LoginRequestTo() {}
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}