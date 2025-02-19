using System;
using FluentValidation;

namespace Application.Features.Marker.Commands;

public class UpdateMarkerCommandValidator : AbstractValidator<UpdateMarkerCommand>
{
    public UpdateMarkerCommandValidator(){
        //RuleFor(x => x.id).NotEmpty();
        RuleFor(x => x.name).NotEmpty().Length(2, 32);
    }
}
