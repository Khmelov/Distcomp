using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using BlogService.Domain.Interfaces;

namespace BlogService.Application.Services;

public class StoryService : BaseService<long, Story, StoryRequestToDto, StoryResponseToDto>, IStoryService
{
    public StoryService(IRepository<long, Story> repository,
        IRequestMapper<StoryRequestToDto, Story> userRequestMapper,
        IResponseMapper<Story, StoryResponseToDto> userResponseMapper) : base(repository, userRequestMapper, userResponseMapper){ }

    public override async Task<StoryResponseToDto> CreateAsync(StoryRequestToDto request)
    {
        var entity = _requestMapper.Map(request);
        entity.Created = DateTime.UtcNow;
        await _repository.AddAsync(entity); 
        return _responseMapper.Map(entity);
    }
}