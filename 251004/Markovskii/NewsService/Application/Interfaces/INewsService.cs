using Application.DTO.Request;
using Application.DTO.Request.News;
using Application.DTO.Response;
using Application.DTO.Response.News;

namespace Application.Interfaces;

public interface INewsService
{
    public Task<NewsResponseToGetById?> CreateNews(NewsRequestToCreate model);
    public Task<IEnumerable<NewsResponseToGetById?>?> GetNews(NewsRequestToGetAll request);
    public Task<NewsResponseToGetById?> GetNewsById(NewsRequestToGetById request);
    public Task<NewsResponseToGetById?> UpdateNews(NewsRequestToFullUpdate model);
    public Task<NewsResponseToGetById?> DeleteNews(NewsRequestToDeleteById request);
}