using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;

namespace RestApiTask.Services.Interfaces
{
    public interface IMarkerService
    {
        Task<IEnumerable<MarkerResponseTo>> GetAllAsync(QueryOptions? options = null);
        Task<MarkerResponseTo> GetByIdAsync(long id);
        Task<MarkerResponseTo> CreateAsync(MarkerRequestTo request);
        Task<MarkerResponseTo> UpdateAsync(long id, MarkerRequestTo request);
        Task DeleteAsync(long id);
    }

}
