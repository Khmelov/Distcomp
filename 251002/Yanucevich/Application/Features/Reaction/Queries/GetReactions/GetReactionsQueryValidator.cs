using System;
using FluentValidation;

namespace Application.Features.Reaction.Queries;

public class GetReactionsQueryValidator : AbstractValidator<GetReactionsQuery>
{
    public GetReactionsQueryValidator(){}

}
