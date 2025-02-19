using System;
using System.Data;
using Domain.Projections;
using FluentValidation;

namespace Application.Features.Reaction.Commands;

public class CreateReactionCommandValidator : AbstractValidator<CreateReactionCommand>
{
    public CreateReactionCommandValidator(){
        RuleFor(x => x.content).NotEmpty().Length(2, 2048);
    }

}
