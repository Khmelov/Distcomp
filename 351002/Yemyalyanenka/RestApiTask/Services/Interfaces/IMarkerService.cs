using RestApiTask.Models.DTOs;

namespace RestApiTask.Services.Interfaces
{
    public interface IMarkerService
    {
        Task<IEnumerable<MarkerResponseTo>> GetAllAsync();
        Task<MarkerResponseTo> GetByIdAsync(long id);
        Task<MarkerResponseTo> CreateAsync(MarkerRequestTo request);
        Task<MarkerResponseTo> UpdateAsync(long id, MarkerRequestTo request);
        Task DeleteAsync(long id);
    }

}
