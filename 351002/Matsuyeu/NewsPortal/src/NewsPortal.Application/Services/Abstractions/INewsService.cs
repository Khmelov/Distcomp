using NewsPortal.Models.Dtos.RequestTo;
using NewsPortal.Models.Repositories.Abstractions;

namespace NewsPortal.Services.Abstractions
{
    public interface INewsService
    {
        Task<IEnumerable<NewsResponseTo>> GetAllNewsAsync();
        Task<NewsResponseTo?> GetNewsByIdAsync(long id);
        Task<NewsResponseTo> CreateNewsAsync(NewsRequestTo newsRequest);
        Task<bool> UpdateNewsAsync(NewsRequestTo newsRequest);
        Task<bool> DeleteNewsAsync(long id);
        Task<PagedResult<NewsResponseTo>> GetPagedNewsAsync(QueryParameters parameters);
        Task<IEnumerable<NewsResponseTo>> GetNewsByCreatorIdAsync(long creatorId);
        Task<IEnumerable<NewsResponseTo>> GetNewsByMarkNameAsync(string markName);
    }
}
