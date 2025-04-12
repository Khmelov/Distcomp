using Application.DTO.Response;
using AutoMapper;
using Domain.Interfaces;
using MediatR;
using Microsoft.EntityFrameworkCore;

namespace Application.Features.Users.Queries;

public class ReadUsersQueryHandler : IRequestHandler<ReadUsersQuery, IEnumerable<UserResponseTo>>
{
    private readonly IUnitOfWork _unitOfWork;
    private readonly IMapper _mapper;

    public ReadUsersQueryHandler(IUnitOfWork unitOfWork, IMapper mapper)
    {
        _unitOfWork = unitOfWork;
        _mapper = mapper;
    }

    public async Task<IEnumerable<UserResponseTo>> Handle(ReadUsersQuery request, CancellationToken cancellationToken)
    {
        var users = await _unitOfWork.User.FindAll(false).ToListAsync(cancellationToken);

        var usersResponseTo = _mapper.Map<IEnumerable<UserResponseTo>>(users);

        return usersResponseTo;
    }
}