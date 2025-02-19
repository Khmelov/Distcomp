using System;
using Domain.Shared;
using MediatR;

namespace Application.Abstractions;

public interface IQuery<TResponse> : IRequest<Result<TResponse>>
{
}
