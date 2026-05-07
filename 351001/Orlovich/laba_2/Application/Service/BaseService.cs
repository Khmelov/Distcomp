using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public abstract class BaseService<T,TResponseTo,TRequestTo> 
    : IService<TResponseTo,TRequestTo>
    where T : class, IIdEntity
    where TResponseTo : class,IIdEntity
    where TRequestTo : class,IIdNullEntity
{
    protected readonly IRepository<T> _repository;
    protected readonly IMapper _mapper;
    
    public BaseService(IRepository<T> repository, IMapper mapper)
    {
        _repository =  repository;
        _mapper = mapper;
    }
    
    public virtual async Task<IList<TResponseTo>> GetAllAsync()
    {
        var list = await _repository.GetAllAsync();
        
        return list.Select(x => _mapper.Map<TResponseTo>(x)).ToList();
    }

    public virtual async Task<TResponseTo> GetByIdAsync(long id)
    {
        return _mapper.Map<TResponseTo>(await _repository.GetByIdAsync(id));
    }

    public virtual async Task<TResponseTo> AddAsync(TRequestTo editor)
    { 
        var a = _mapper.Map<T>(editor);
        
        await _repository.AddAsync(a);
        return _mapper.Map<TResponseTo>(a);
    }

    public virtual async Task<TResponseTo?> UpdateAsync(long id, TRequestTo t)
    {
        var a = _mapper.Map<T>(t);
        a = await _repository.UpdateAsync(id,a);
        
        if(a == null)
            return null;
        
        return _mapper.Map<TResponseTo>(a);
    }

    public virtual async Task<int> DeleteAsync(long id)
    {
        return await _repository.DeleteAsync(id);
    }
}