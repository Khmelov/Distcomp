package by.bsuir.rest.repository;

public record UserFilter(String login, String firstname, String lastname) {

    public static UserFilter empty() {
        return new UserFilter(null, null, null);
    }
}
