using Distcomp.Application.DTOs;

namespace Distcomp.Application.Interfaces
{
    public interface IMarkerService
    {
        MarkerResponseTo Create(MarkerRequestTo request);
        MarkerResponseTo? GetById(long id);
        IEnumerable<MarkerResponseTo> GetAll();
        MarkerResponseTo Update(long id, MarkerRequestTo request);
        bool Delete(long id);
    }
}