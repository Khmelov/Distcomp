using Microsoft.AspNetCore.Mvc;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Services.Interfaces;
using Publisher.Presentation.Controllers.Abstractions;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Publisher.Presentation.Controllers;


public class CommentsController : BaseController<CommentRequestTo, CommentResponseTo> {
    public CommentsController(ICommentService service)
        : base(service) {
    }
}