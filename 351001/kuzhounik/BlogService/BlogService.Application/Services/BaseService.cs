using BlogService.Application.DependencyInjection.Interfaces.DTOs;
using BlogService.Application.Interfaces.Mappers;
using BlogService.Application.Interfaces.Services;
using BlogService.Domain.Interfaces;

namespace BlogService.Application.Services;

public abstract class BaseService<ID, Entity, RequestDto, ResponseDto>
    : IService<ID, RequestDto, ResponseDto>
    where Entity : class, IEntity<ID>
    where RequestDto : class, IRequestDto<ID>
    where ResponseDto : class, IResponseDto<ID>
{
    protected readonly IRepository<ID, Entity> _repository;
    protected readonly IRequestMapper<RequestDto, Entity> _requestMapper;
    protected readonly IResponseMapper<Entity, ResponseDto> _responseMapper;
    
    protected BaseService(IRepository<ID, Entity> repository,
        IRequestMapper<RequestDto, Entity> requestMapper,
        IResponseMapper<Entity, ResponseDto> responseMapper)
    {
        _repository = repository;
        _requestMapper = requestMapper;
        _responseMapper = responseMapper;
    }
    
    public virtual async Task<ResponseDto> CreateAsync(RequestDto request)
    {
        var entity = _requestMapper.Map(request); 
        await _repository.AddAsync(entity); 
        return _responseMapper.Map(entity);
    }

    public virtual async Task<ResponseDto?> GetAsync(ID id)
    {
        var entity = await _repository.GetByIdAsync(id);
        if (entity == null) 
            return null;
        return _responseMapper.Map(entity);
    }

    public virtual async Task<IEnumerable<ResponseDto>> GetAllAsync()
    {
        var entities = await _repository.GetAllAsync();
        return entities.Select(entity => _responseMapper.Map(entity));
    }

    public virtual async Task<ResponseDto?> UpdateAsync(RequestDto request)
    {
        var existing = await _repository.GetByIdAsync(request.ID);
        if (existing is null)
            return null;

        var updatedEntity = _requestMapper.Map(request);
        updatedEntity.ID = request.ID;

        await _repository.UpdateAsync(updatedEntity);

        return _responseMapper.Map(updatedEntity);
    }

    public virtual async Task DeleteAsync(ID id)
    {
        await _repository.DeleteAsync(id);
    }
}