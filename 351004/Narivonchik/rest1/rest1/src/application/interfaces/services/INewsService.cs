using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;

namespace rest1.application.interfaces.services;

public interface INewsService
{
    Task<NewsResponseTo> CreateNews(NewsRequestTo createNewsRequestTo);

    Task<IEnumerable<NewsResponseTo>> GetAllNews();

    Task<NewsResponseTo> GetNews(NewsRequestTo getNewsRequestTo);

    Task<NewsResponseTo> UpdateNews(NewsRequestTo updateNewsRequestTo);

    Task DeleteNews(NewsRequestTo deleteNewsRequestTo);   
}