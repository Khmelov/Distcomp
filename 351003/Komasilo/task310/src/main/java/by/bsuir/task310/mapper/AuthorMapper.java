package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.AuthorRequestTo;
import by.bsuir.task310.dto.AuthorResponseTo;
import by.bsuir.task310.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequestTo requestTo) {
        Author author = new Author();
        author.setId(requestTo.getId());
        author.setLogin(requestTo.getLogin());
        author.setPassword(requestTo.getPassword());
        author.setFirstname(requestTo.getFirstname());
        author.setLastname(requestTo.getLastname());
        author.setRole(requestTo.getRole() == null ? "CUSTOMER" : requestTo.getRole());
        return author;
    }

    public AuthorResponseTo toResponseTo(Author author) {
        AuthorResponseTo responseTo = new AuthorResponseTo();
        responseTo.setId(author.getId());
        responseTo.setLogin(author.getLogin());
        responseTo.setFirstname(author.getFirstname());
        responseTo.setLastname(author.getLastname());
        responseTo.setRole(author.getRole());
        return responseTo;
    }
}