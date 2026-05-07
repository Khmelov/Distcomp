using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Repositories.Abstractions;

namespace NewsPortal.Services.Abstractions
{
    public interface IMarkService
    {
        Task<IEnumerable<MarkResponseTo>> GetAllMarksAsync();
        Task<MarkResponseTo?> GetMarkByIdAsync(long id);
        Task<MarkResponseTo> CreateMarkAsync(MarkRequestTo markRequest);
        Task<bool> UpdateMarkAsync(MarkRequestTo markRequest);
        Task<bool> DeleteMarkAsync(long id);
        Task<PagedResult<MarkResponseTo>> GetPagedMarksAsync(QueryParameters parameters);
    }
}
