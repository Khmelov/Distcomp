using System;
using FluentValidation;

namespace Application.Features.User.Queries;

public class GetUserByIdQueryValidator : AbstractValidator<GetUserByIdQuery>
{
    public GetUserByIdQueryValidator(){}
}
