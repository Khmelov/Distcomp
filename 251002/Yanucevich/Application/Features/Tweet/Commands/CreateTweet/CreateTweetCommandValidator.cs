using System;
using System.Data;
using FluentValidation;

namespace Application.Features.Tweet.Commands;

public class CreateTweetCommandValidator : AbstractValidator<CreateTweetCommand>
{
    public CreateTweetCommandValidator(){
        RuleFor(x => x.title).NotEmpty().Length(2, 64);
        RuleFor(x => x.content).NotEmpty().Length(4, 2048);
    }

}
