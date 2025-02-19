using System;
using FluentValidation;

namespace Application.Features.Tweet.Commands;

public class UpdateTweetCommandValidator : AbstractValidator<UpdateTweetCommand>
{
    public UpdateTweetCommandValidator(){
        RuleFor(x => x.title).NotEmpty().Length(2, 64);
        RuleFor(x => x.content).NotEmpty().Length(4, 2048);
    }

}
