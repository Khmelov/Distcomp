using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using Shared.Application.Interfaces.Services;

namespace BlogService.Application.Interfaces.Services;

public interface IStoryService<Id> : IService<Id, StoryRequestToDto<Id>, StoryResponseToDto<Id>>
{
    
}