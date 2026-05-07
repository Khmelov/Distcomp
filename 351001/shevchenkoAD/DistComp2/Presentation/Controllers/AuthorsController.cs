using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Services.Interfaces;
using Presentation.Controllers.Abstractions;

namespace Presentation.Controllers;

public class AuthorsController : BaseController<AuthorRequestTo, AuthorResponseTo> {
    public AuthorsController(IAuthorService service)
        : base(service) {
    }
}