using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;

namespace Task310RestApi.Interfaces
{
    public interface INewsService
    {
        Task<IEnumerable<NewsResponseTo>> GetAllNewsAsync();
        Task<NewsResponseTo?> GetNewsByIdAsync(long id);
        Task<NewsResponseTo> CreateNewsAsync(NewsRequestTo newsRequest);
        Task<NewsResponseTo?> UpdateNewsAsync(long id, NewsRequestTo newsRequest);
        Task<bool> DeleteNewsAsync(long id);
        Task<bool> ExistsAsync(long id);
        Task<IEnumerable<NewsResponseTo>> GetNewsByParamsAsync(
            List<string>? labelNames,
            List<long>? labelIds,
            string? creatorLogin,
            string? title,
            string? content);
    }
}