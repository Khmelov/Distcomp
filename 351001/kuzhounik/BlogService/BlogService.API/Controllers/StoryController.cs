using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using Microsoft.AspNetCore.Mvc;

namespace BlogService.API.Controllers;

[ApiController]
[Route("api/v1.0/stories")]
public class StoryController : BaseController<StoryRequestToDto, StoryResponseToDto>
{
    public StoryController(IStoryService service) : base(service) { }
}