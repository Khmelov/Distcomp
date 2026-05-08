using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Services.Interfaces;
using Presentation.Controllers.Abstractions;

namespace Presentation.Controllers;

public class CommentsController : BaseController<CommentRequestTo, CommentResponseTo> {
    public CommentsController(ICommentService service)
        : base(service) {
    }
}