using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Services.Interfaces;
using Presentation.Controllers.Abstractions;

namespace Presentation.Controllers;

public class LabelsController : BaseController<LabelRequestTo, LabelResponseTo> {
    public LabelsController(ILabelService service)
        : base(service) {
    }
}