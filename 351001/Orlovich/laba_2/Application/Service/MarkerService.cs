using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class MarkerService
    : BaseService<Marker,MarkerResponseTo,MarkerRequestTo>
{
    public MarkerService(IRepository<Marker> repository, IMapper mapper) : base(repository, mapper)
    {
        
    }
}