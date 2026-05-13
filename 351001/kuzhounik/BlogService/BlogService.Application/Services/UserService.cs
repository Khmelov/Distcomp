using BlogService.Application.DTOs.Request;
using BlogService.Application.DTOs.Response;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Entities;
using Microsoft.Extensions.Caching.Distributed;
using Shared.Application.Interfaces.Mappers;
using Shared.Application.Services;
using Shared.Domain.Interfaces;

namespace BlogService.Application.Services;

public class UserService<Id> : BaseService<Id, User<Id>, UserRequestToDto<Id>, UserResponseToDto<Id>>, IUserService<Id>
{
    public UserService(IRepository<Id, User<Id>> repository,
        IRequestMapper<UserRequestToDto<Id>, User<Id>> userRequestMapper,
        IResponseMapper<User<Id>, UserResponseToDto<Id>> userResponseMapper, 
        IDistributedCache cache) : 
        base(repository, userRequestMapper, userResponseMapper,  cache){ }
    
    protected override async Task OnBeforeCreateAsync(UserRequestToDto<Id> request)
    {
        if (await _repository.ExistsAsync(u => u.Login == request.Login))
        {
            throw new HttpRequestException("User with the same login already exists");
        }
    }

    public virtual async Task<UserResponseToDto<Id>> CreateAsync(UserRequestToDto<Id> request)
    {
        request.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);
        
        var entity = _requestMapper.Map(request); 
        await _repository.AddAsync(entity); 
        
        
        // Инвалидируем кэш, чтобы гарантировать чистоту
        if (_cache != null)
        {
            await _cache.RemoveAsync(GetCacheKey(entity.ID));
        }

        return _responseMapper.Map(entity);
    }
    
    public async override Task<UserResponseToDto<Id>?> UpdateAsync(UserRequestToDto<Id> entityRequest)
    {
        if (!string.IsNullOrEmpty(entityRequest.Password) && !entityRequest.Password.StartsWith("$2a$"))
        {
            entityRequest.Password = BCrypt.Net.BCrypt.HashPassword(entityRequest.Password);
        }
        return await base.UpdateAsync(entityRequest);
    }
}