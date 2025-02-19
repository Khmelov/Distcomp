using System;
using FluentValidation;

namespace Application.Features.Tweet.Commands;

public class DeleteTweetCommandValidator : AbstractValidator<DeleteTweetCommand>
{
    public DeleteTweetCommandValidator(){}

}
