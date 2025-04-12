using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Entities;
using Domain.Interfaces;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.Users.Commands;

public class CreateUserCommandHandler : IRequestHandler<CreateUserCommand, UserResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public CreateUserCommandHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<UserResponseTo> Handle(CreateUserCommand request, CancellationToken cancellationToken)
    {
        var user = await _unitOfWork.User.FindByCondition(user => user.Login == request.UserRequestTo.Login, false).SingleOrDefaultAsync(cancellationToken: cancellationToken);

        if (user != null)
        {
            throw new AlreadyExistsException(string.Format(ExceptionMessages.UserAlreadyExists, request.UserRequestTo.Login));
        }
        
        user = _mapper.Map<User>(request.UserRequestTo);

        _unitOfWork.User.Create(user);

        await _unitOfWork.SaveChangesAsync(cancellationToken);

        var userResponseTo = _mapper.Map<UserResponseTo>(user);

        return userResponseTo;
    }
}