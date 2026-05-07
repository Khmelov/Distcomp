using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class EditorService 
    : BaseService<Editor,EditorResponseTo,EditorRequestTo>
{
    public EditorService(IRepository<Editor> repository, IMapper mapper) : base(repository, mapper)
    {
        
    }
}