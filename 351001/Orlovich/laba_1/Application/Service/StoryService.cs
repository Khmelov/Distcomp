using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class StoryService
    : BaseService<Story,StoryResponseTo,StoryRequestTo>
{
    public StoryService(IRepository<Story> repository, IMapper mapper) : base(repository, mapper)
    {
        
    }
}