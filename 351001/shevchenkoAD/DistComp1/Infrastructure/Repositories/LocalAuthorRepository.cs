using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class LocalAuthorRepository : LocalBaseRepository<Author> {
    public LocalAuthorRepository() {
        _storage.Add(new Author {
            Id = 1,
            Login = "alexander.shevchenko.bsuir@gmail.com",
            Password = "DefaultPassword123",
            Firstname = "Александр",
            Lastname = "Шевченко"
        });
    }

    protected override Author Copy(Author src) {
        return new Author {
            Id = src.Id,
            Login = src.Login,
            Password = src.Password,
            Firstname = src.Firstname,
            Lastname = src.Lastname
        };
    }
}