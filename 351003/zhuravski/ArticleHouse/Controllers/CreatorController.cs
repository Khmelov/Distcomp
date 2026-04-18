using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace ArticleHouse.Controllers;

[ApiController]
[Route("api/v2.0")]
[Authorize]
public class CreatorController : ControllerBase
{
    private readonly ICreatorService creatorService;
    private readonly IAuthService authService;
    private readonly ILogger<CreatorController> logger;

    public CreatorController(ICreatorService creatorService, ILogger<CreatorController> logger,
                                IAuthService authService)
    {
        this.creatorService = creatorService;
        this.logger = logger;
        this.authService = authService;
    }

    [HttpGet("creators")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<ActionResult<CreatorResponseDTO[]>> GetAll() {
        return Ok(await creatorService.GetAllCreatorsAsync());
    }

    [HttpPost("creators")]
    [AllowAnonymous]
    public async Task<ActionResult<CreatorResponseDTO>> Register([FromBody] CreatorRegistrationDTO dto)
    {
        CreatorResponseDTO responseDTO = await authService.RegisterAsync(dto);
        return Created($"/api/v2.0/creators{responseDTO.Id}", responseDTO);
    }

    [HttpPost("login")]
    [AllowAnonymous]
    public async Task<ActionResult<AuthResponseDTO>> Login([FromBody] LoginRequestDTO dto)
    {
        AuthResponseDTO responseDTO = await authService.LoginAsync(dto);
        return Ok(responseDTO);
    }

    [HttpGet("creators/{id}")]
    public async Task<ActionResult<CreatorResponseDTO>> GetById(long id)
    {
        return Ok(await creatorService.GetCreatorByIdAsync(id));
    }

    [HttpDelete("creators/{id}")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<ActionResult> DeleteById(long id)
    {
        await creatorService.DeleteCreatorAsync(id);
        return Ok();
    }

    [HttpPut("creators/{id}")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<ActionResult> Update(long id, CreatorRequestDTO dto)
    {
        return Ok(await creatorService.UpdateCreatorByIdAsync(id, dto));
    }

    [HttpPut("creators")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<ActionResult> Update(CreatorRequestDTO dto)
    {
        if (dto.Id == null)
        {
            return BadRequest();
        }
        return Ok(await creatorService.UpdateCreatorByIdAsync(dto.Id.Value, dto));
    }
}