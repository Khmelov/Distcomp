using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Services.Interfaces;
using Presentation.Controllers.Abstractions;

namespace Presentation.Controllers;

public class IssuesController : BaseController<IssueRequestTo, IssueResponseTo> {
    public IssuesController(IIssueService service)
        : base(service) {
    }
}