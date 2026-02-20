using Application.Interfaces;
using Core.Entities;

namespace Infrastructure.Persistence.InMemory
{
    public class MarkerInMemoryRepository : InMemoryRepository<Marker>, IMarkerRepository
    {

    }
}
