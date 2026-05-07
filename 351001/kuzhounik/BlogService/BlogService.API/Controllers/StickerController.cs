using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;

namespace BlogService.API.Controllers;

[ApiController]
[Route("api/v1.0/stickers")]
public class StickerController : BaseController<StickerRequestToDto, StickerResponseToDto>
{
    public StickerController(IStickerService service) : base(service) { }
}