using System;
using FluentValidation;

namespace Application.Features.Reaction.Queries;

public class GetReactionByIdQueryValidator : AbstractValidator<GetReactionByIdQuery>
{
    public GetReactionByIdQueryValidator(){}

}
