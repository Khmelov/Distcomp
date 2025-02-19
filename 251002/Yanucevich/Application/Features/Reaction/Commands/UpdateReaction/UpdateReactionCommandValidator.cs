using System;
using System.Data;
using FluentValidation;

namespace Application.Features.Reaction.Commands;

public class UpdateReactionCommandValidator : AbstractValidator<UpdateReactionCommand>
{
    public UpdateReactionCommandValidator(){
        RuleFor(x => x.content).NotEmpty().Length(2, 2048);
    }
}
