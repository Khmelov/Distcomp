using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Application.Services.Interfaces;
using DistComp.Presentation.Controllers.Abstractions;

namespace DistComp.Presentation.Controllers;

public class CommentsController : BaseController<CommentRequestTo, CommentResponseTo> {
    public CommentsController(ICommentService service)
        : base(service) {
    }
}