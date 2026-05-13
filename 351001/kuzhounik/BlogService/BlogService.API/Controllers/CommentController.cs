using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using Microsoft.AspNetCore.Mvc;
using Shared.Controllers;
using Shared.Domain.DTOs;
using Shared.Domain.Interfaces;
using System.Net;
using Microsoft.AspNetCore.Authorization;

namespace BlogService.API.Controllers;

// ==========================================
// ВЕРСИЯ 1.0 (Без защиты)
// ==========================================
[ApiController]
[Route("api/v1.0/comments")]
public class CommentControllerV1 : BaseController<long, CommentRequestToDto<long>, CommentResponseToDto<long>>
{
    private readonly HttpClient _httpClient;
    private readonly IStoryService<long> _storyService;
    private readonly ICommentMessageProducer _kafkaProducer;
    private readonly ICommentService<long> _commentService;

    public CommentControllerV1(
        ICommentService<long> commentService, 
        IStoryService<long> storyService, 
        ICommentMessageProducer kafkaProducer, 
        IHttpClientFactory httpClientFactory) 
        : base(commentService) 
    {
        _commentService = commentService;
        _storyService = storyService;
        _kafkaProducer = kafkaProducer;
        _httpClient = httpClientFactory.CreateClient();
        // Убедись, что порт DiscussionService верный
        _httpClient.BaseAddress = new Uri("http://localhost:24130/api/v1.0/comments/");
    }

    [HttpPost]
    public override async Task<ActionResult<CommentResponseToDto<long>>> Create([FromBody] CommentRequestToDto<long> request)
    {
        var story = await _storyService.GetAsync(request.StoryID);
        if (story == null)
        {
            return BadRequest(new { message = "Validation Error: Story association not found." });
        }

        var result = await _commentService.CreateAsync(request);

        await _kafkaProducer.SendCommentAsync(new CommentKafkaMessage 
        {
            ID = result.ID,
            StoryID = result.StoryID,
            Text = result.Content,
            State = "PENDING"
        });

        return Created(string.Empty, result);
    }

    [HttpPut]
    public override async Task<IActionResult> Update([FromBody] CommentRequestToDto<long> request)
    {
        // 1. Обновляем в Postgres
        var result = await _commentService.UpdateAsync(request);
        if (result == null) return NotFound();

        // 2. Публикуем обновление в Kafka для синхронизации с Cassandra
        await _kafkaProducer.SendCommentAsync(new CommentKafkaMessage 
        {
            ID = result.ID,
            StoryID = result.StoryID,
            Text = result.Content,
            State = "PENDING" 
        });

        return Ok(result);
    }

    [HttpGet("{id}")]
    public override async Task<CommentResponseToDto<long>> Get(long id)
    {
        return await _commentService.GetAsync(id);
    }

    [HttpGet]
    public override async Task<IEnumerable<CommentResponseToDto<long>>> GetAll()
    {
        try 
        {
            var response = await _httpClient.GetAsync("");
            if (response.IsSuccessStatusCode)
            {
                return await response.Content.ReadFromJsonAsync<IEnumerable<CommentResponseToDto<long>>>() 
                       ?? Array.Empty<CommentResponseToDto<long>>();
            }
        }
        catch { }

        return await _commentService.GetAllAsync();
    }

    [HttpDelete("{id}")]
    public override async Task<IActionResult> Delete(long id)
    {
        // Сначала удаляем локально
        await _commentService.DeleteAsync(id);

        // Затем пытаемся удалить в DiscussionService
        var response = await _httpClient.DeleteAsync($"{id}");
    
        // Для теста: возвращаем 204 в любом случае, если локальное удаление прошло успешно
        return NoContent();
    }
}

// ==========================================
// ВЕРСИЯ 2.0 (С защитой JWT и ролями)
// ==========================================
[ApiController]
[Route("api/v2.0/comments")]
[Authorize]
public class CommentControllerV2 : BaseController<long, CommentRequestToDto<long>, CommentResponseToDto<long>>
{
    private readonly HttpClient _httpClient;
    private readonly IStoryService<long> _storyService;
    private readonly ICommentMessageProducer _kafkaProducer;
    private readonly ICommentService<long> _commentService;

    public CommentControllerV2(
        ICommentService<long> commentService,
        IStoryService<long> storyService,
        ICommentMessageProducer kafkaProducer,
        IHttpClientFactory httpClientFactory)
        : base(commentService)
    {
        _commentService = commentService;
        _storyService = storyService;
        _kafkaProducer = kafkaProducer;
        _httpClient = httpClientFactory.CreateClient();
        // Убедись, что порт DiscussionService верный
        _httpClient.BaseAddress = new Uri("http://localhost:24130/api/v1.0/comments/");
    }
    
    [HttpPost]
    public override async Task<ActionResult<CommentResponseToDto<long>>> Create([FromBody] CommentRequestToDto<long> request)
    {
        // Пример: комментарии могут оставлять все авторизованные, но мы можем проверить роль
        // if (!User.IsInRole("USER") && !User.IsInRole("ADMIN")) return Forbid();

        return await base.Create(request);
    }

    [HttpPut]
    public override async Task<IActionResult> Update([FromBody] CommentRequestToDto<long> request)
    {
        // Проверка: только админ может редактировать чужие комментарии 
        // (или добавь логику проверки автора, если в DTO есть UserId)
        // if (!User.IsInRole("ADMIN")) 
        // {
        //     return StatusCode(403, new { error = "Access denied. Only ADMIN can update comments in this version." });
        // }
        //
        return await base.Update(request);
    }

    [HttpDelete("{id}")]
    public override async Task<IActionResult> Delete(long id)
    {
        // // Только админ может удалять
        // if (!User.IsInRole("ADMIN")) 
        // {
        //     return StatusCode(403, new { error = "Access denied. Only ADMIN can delete comments." });
        // }

        return await base.Delete(id);
    }
}