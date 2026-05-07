using AutoMapper;
using MediatR;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Domain.Entities;
using RW.Domain.Interfaces;

namespace RW.Application.Features.Authors.Commands;

public record CreateAuthorCommand(AuthorRequestTo Dto) : IRequest<AuthorResponseTo>;

public class CreateAuthorHandler : IRequestHandler<CreateAuthorCommand, AuthorResponseTo>
{
    private readonly IRepository<Author> _repository;
    private readonly IMapper _mapper;

    public CreateAuthorHandler(IRepository<Author> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<AuthorResponseTo> Handle(CreateAuthorCommand request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrEmpty(request.Dto.Login) || request.Dto.Login.Length < 2 || request.Dto.Login.Length > 64)
            throw new Exceptions.ValidationException("Login must be between 2 and 64 characters.");
        if (string.IsNullOrEmpty(request.Dto.Password) || request.Dto.Password.Length < 8 || request.Dto.Password.Length > 128)
            throw new Exceptions.ValidationException("Password must be between 8 and 128 characters.");
        if (string.IsNullOrEmpty(request.Dto.FirstName) || request.Dto.FirstName.Length < 2 || request.Dto.FirstName.Length > 64)
            throw new Exceptions.ValidationException("FirstName must be between 2 and 64 characters.");
        if (string.IsNullOrEmpty(request.Dto.LastName) || request.Dto.LastName.Length < 2 || request.Dto.LastName.Length > 64)
            throw new Exceptions.ValidationException("LastName must be between 2 and 64 characters.");

        var entity = _mapper.Map<Author>(request.Dto);
        var created = await _repository.CreateAsync(entity);
        return _mapper.Map<AuthorResponseTo>(created);
    }
}
