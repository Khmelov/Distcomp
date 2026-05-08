using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;

namespace RestApiTask.Services.Interfaces
{
    public interface IArticleService
    {
        Task<IEnumerable<ArticleResponseTo>> GetAllAsync(QueryOptions? options = null);
        Task<ArticleResponseTo> GetByIdAsync(long id);
        Task<ArticleResponseTo> CreateAsync(ArticleRequestTo request);
        Task<ArticleResponseTo> UpdateAsync(long id, ArticleRequestTo request);
        Task DeleteAsync(long id);
    }

}
