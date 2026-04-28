using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Services.Interfaces;
using Publisher.Presentation.Controllers.Abstractions;

namespace Publisher.Presentation.Controllers;

public class LabelsController : BaseController<LabelRequestTo, LabelResponseTo>
{
    public LabelsController(ILabelService service)
        : base(service)
    {
    }
}