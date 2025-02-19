using System;
using FluentValidation;

namespace Application.Features.Reaction.Commands;

public class DeleteReactionCommandValidator : AbstractValidator<DeleteReactionCommand>
{
    public DeleteReactionCommandValidator(){}

}
