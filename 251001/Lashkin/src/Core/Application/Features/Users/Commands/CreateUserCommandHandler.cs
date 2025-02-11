using Application.DTO.Response;
using AutoMapper;
using Domain.Entities;
using Domain.Repositories;
using MediatR;

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
        var user = _mapper.Map<User>(request.UserRequestTo);
        
        _unitOfWork.User.Create(user);

        await _unitOfWork.SaveChangesAsync();
        
        var userResponseTo = _mapper.Map<UserResponseTo>(user);
        
        return userResponseTo;
    }
}