using Application.Repository;
using Application.Service;
using Domain.Models;
using Infrastructe.Repository.DbPostgresRepository;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0/authors")]
public class AuthorController : BaseController<AuthorResponseTo,AuthorRequestTo>
{
    public AuthorController(IService<AuthorResponseTo, AuthorRequestTo> service) : base(service)
    {
    }
}