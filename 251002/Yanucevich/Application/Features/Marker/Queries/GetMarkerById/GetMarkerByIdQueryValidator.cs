using System;
using FluentValidation;

namespace Application.Features.Marker.Queries;

public class GetMarkerByIdQueryValidator : AbstractValidator<GetMarkerByIdQuery>
{
    public GetMarkerByIdQueryValidator(){
        //RuleFor(x => x.id).NotEmpty();
    }

}
