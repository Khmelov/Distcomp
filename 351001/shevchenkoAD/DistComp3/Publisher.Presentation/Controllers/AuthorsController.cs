using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Services.Interfaces;
using Publisher.Presentation.Controllers.Abstractions;

namespace Publisher.Presentation.Controllers;

public class AuthorsController : BaseController<AuthorRequestTo, AuthorResponseTo> {
    public AuthorsController(IAuthorService service)
        : base(service) {
    }
}