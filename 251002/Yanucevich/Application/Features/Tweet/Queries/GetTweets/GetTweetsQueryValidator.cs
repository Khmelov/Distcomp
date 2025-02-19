using System;
using FluentValidation;

namespace Application.Features.Tweet.Queries;

public class GetTweetsQueryValidator : AbstractValidator<GetTweetsQuery>
{
    public GetTweetsQueryValidator(){}

}
