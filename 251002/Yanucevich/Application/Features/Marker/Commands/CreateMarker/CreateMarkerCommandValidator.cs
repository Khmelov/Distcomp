using System;
using FluentValidation;

namespace Application.Features.Marker.Commands;

public class CreateMarkerCommandValidator 
    : AbstractValidator<CreateMarkerCommand>
{
    public CreateMarkerCommandValidator(){
        RuleFor(x => x.Name).NotEmpty().Length(2,32);
    }
}
