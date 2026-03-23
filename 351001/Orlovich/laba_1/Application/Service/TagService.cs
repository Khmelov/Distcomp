using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class TagService
    : BaseService<Tag,TagResponseTo,TagRequestTo>
{
    public TagService(IRepository<Tag> repository, IMapper mapper) : base(repository, mapper)
    {
        
    }
}