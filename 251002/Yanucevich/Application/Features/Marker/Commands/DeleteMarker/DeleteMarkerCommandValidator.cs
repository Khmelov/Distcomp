using System;
using FluentValidation;

namespace Application.Features.Marker.Commands;

public class DeleteMarkerCommandValidator : AbstractValidator<DeleteMarkerCommand>
{
    public DeleteMarkerCommandValidator(){
        //RuleFor(x => x.id).NotEmpty();
    }
}
