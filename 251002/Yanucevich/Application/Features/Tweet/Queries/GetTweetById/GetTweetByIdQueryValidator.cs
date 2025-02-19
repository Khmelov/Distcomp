using System;
using FluentValidation;

namespace Application.Features.Tweet.Queries;

public class GetTweetByIdQueryValidator : AbstractValidator<GetTweetByIdQuery>
{
    public GetTweetByIdQueryValidator(){}

}
