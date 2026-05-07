using Application.DTOs.Requests;
using Application.DTOs.Responses;

namespace Application.Interfaces
{
    public interface INewsService
    {
        Task<NewsResponseTo> CreateNews(NewsRequestTo createNewsRequestTo);

        Task<IEnumerable<NewsResponseTo>> GetAllNews();

        Task<NewsResponseTo> GetNews(NewsRequestTo getNewsRequestTo);

        Task<NewsResponseTo> UpdateNews(NewsRequestTo updateNewsRequestTo);

        Task DeleteNews(NewsRequestTo deleteNewsRequestTo);   
    }
}
