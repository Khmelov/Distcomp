using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Services;
using Shared.Domain.Interfaces;

namespace BlogService.Application.Services;

public class StoryService<Id> : BaseService<Id, Story<Id>, StoryRequestToDto<Id>, StoryResponseToDto<Id>>, IStoryService<Id>
{
    private readonly IRepository<Id, User<Id>> _userRepository;

    public StoryService(IRepository<Id, User<Id>> userRepository, IRepository<Id, Story<Id>> repository,
        IRequestMapper<StoryRequestToDto<Id>, Story<Id>> userRequestMapper,
        IResponseMapper<Story<Id>, StoryResponseToDto<Id>> userResponseMapper, 
        IDistributedCache cache) : 
        base(repository, userRequestMapper, userResponseMapper, cache)
    {
        _userRepository = userRepository;
    }

    protected override async Task OnBeforeCreateAsync(StoryRequestToDto<Id> request)
    {
        if (await _userRepository.GetByIdAsync(request.UserID) == null)
        {
            throw new HttpRequestException("User not found");
        }
    }
}