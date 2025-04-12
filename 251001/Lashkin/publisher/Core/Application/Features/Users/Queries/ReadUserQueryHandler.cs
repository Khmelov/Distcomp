using Application.DTO.Response;
using Application.Exceptions;
using AutoMapper;
using Domain.Interfaces;
using MediatR;

namespace Application.Features.Users.Queries;

public class ReadUserQueryHandler : IRequestHandler<ReadUserQuery, UserResponseTo>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadUserQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<UserResponseTo> Handle(ReadUserQuery request, CancellationToken cancellationToken)
    {
        var user = await _unitOfWork.User.FindByIdAsync(request.Id, cancellationToken);

        if (user == null)
        {
            throw new NotFoundException(string.Format(ExceptionMessages.UserNotFound, request.Id));
        }

        var userResponseTo = _mapper.Map<UserResponseTo>(user);

        return userResponseTo;
    }
}