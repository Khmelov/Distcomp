using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Application.Services.Interfaces;
using DistComp.Presentation.Controllers.Abstractions;

namespace DistComp.Presentation.Controllers;

public class LabelsController : BaseController<LabelRequestTo, LabelResponseTo> {
    public LabelsController(ILabelService service)
        : base(service) {
    }
}