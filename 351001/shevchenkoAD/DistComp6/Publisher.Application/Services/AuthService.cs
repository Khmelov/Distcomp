using AutoMapper;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;
using BC = BCrypt.Net.BCrypt;

namespace Publisher.Application.Services;

public class AuthService : IAuthService
{
    private readonly IMapper _mapper;
    private readonly IRepository<Author> _repository;
    private readonly ITokenProvider _tokenProvider;

    public AuthService(IRepository<Author> repository, ITokenProvider tokenProvider, IMapper mapper)
    {
        _repository = repository;
        _tokenProvider = tokenProvider;
        _mapper = mapper;
    }

    public async Task<AuthResponseTo> LoginAsync(LoginRequestTo request)
    {
        var authors = await _repository.GetAllAsync();
        var author = authors.FirstOrDefault(a => a.Login == request.Login);

        if (author == null || !BC.Verify(request.Password, author.Password))
            throw new RestException(401, 01, "Invalid login or password");

        var token = _tokenProvider.GenerateToken(author);
        return new AuthResponseTo(token);
    }

    public async Task<AuthorResponseTo> RegisterAsync(AuthorRequestTo request)
    {
        var authors = await _repository.GetAllAsync();
        if (authors.Any(a => a.Login == request.Login))
            throw new RestException(403, 16, "Login already taken");

        var author = _mapper.Map<Author>(request);

        author.Password = BC.HashPassword(request.Password);

        var created = await _repository.CreateAsync(author);
        return _mapper.Map<AuthorResponseTo>(created);
    }
}