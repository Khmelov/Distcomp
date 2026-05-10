using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using DataAccess.Models;
using Infrastructure.Exceptions;
using Microsoft.Extensions.Caching.Distributed;
using BusinessLogic.Repositories;

public class CreatorService : BaseService<Creator, CreatorRequestTo, CreatorResponseTo>
{
    public CreatorService(IRepository<Creator> repository, IMapper mapper, IDistributedCache cache)
        : base(repository, mapper, cache)
    { }

    public async override Task<CreatorResponseTo> CreateAsync(CreatorRequestTo entityRequest)
    {
        var allCreators = await _repository.GetAllAsync();
        if (allCreators.Any(c => c.Login == entityRequest.Login))
        {
            throw new BaseException(403, "Creator with such login already exists");
        }

        // Хэшируем пароль перед сохранением
        entityRequest.Password = BCrypt.Net.BCrypt.HashPassword(entityRequest.Password);

        Creator creator = _mapper.Map<Creator>(entityRequest);
        creator.Id = await _repository.GetLastIdAsync() + 1;

        await _repository.CreateAsync(creator);

        var response = _mapper.Map<CreatorResponseTo>(creator);
        await SetCacheAsync(GetCacheKey(creator.Id), response);
        await InvalidateAllCacheAsync();

        return response;
    }

    public async override Task<CreatorResponseTo?> UpdateAsync(CreatorRequestTo entityRequest)
    {
        // Если пароль обновляется, его тоже нужно захешировать
        if (!string.IsNullOrEmpty(entityRequest.Password) && !entityRequest.Password.StartsWith("$2a$"))
        {
            entityRequest.Password = BCrypt.Net.BCrypt.HashPassword(entityRequest.Password);
        }
        return await base.UpdateAsync(entityRequest);
    }
}
