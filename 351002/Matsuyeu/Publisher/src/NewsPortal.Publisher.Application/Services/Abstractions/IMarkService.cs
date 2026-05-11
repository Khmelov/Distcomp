using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions
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
