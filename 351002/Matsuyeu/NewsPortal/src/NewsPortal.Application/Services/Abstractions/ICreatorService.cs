using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Repositories.Abstractions;

namespace NewsPortal.Services.Abstractions
{
    public interface ICreatorService
    {
        Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync();
        Task<CreatorResponseTo?> GetCreatorByIdAsync(long id);
        Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest);
        Task<bool> UpdateCreatorAsync(CreatorRequestTo creatorRequest);
        Task<bool> DeleteCreatorAsync(long id);
        Task<PagedResult<CreatorResponseTo>> GetPagedCreatorsAsync(QueryParameters parameters);
    }
}
