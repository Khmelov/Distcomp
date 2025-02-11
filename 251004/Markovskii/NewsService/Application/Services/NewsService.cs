using Application.DTO.Request;
using Application.DTO.Request.News;
using Application.DTO.Response;
using Application.DTO.Response.News;
using Application.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Application.Services;

public class NewsService(INewsRepository _newsRepository, IMapper _mapper) : INewsService
{
    public async Task<NewsResponseToGetById?> CreateNews(NewsRequestToCreate model)
    {
        News news = _mapper.Map<News>(model);
        Validate(news);
        news = await _newsRepository.AddNews(news);
        return _mapper.Map<NewsResponseToGetById>(news);
    }

    public async Task<IEnumerable<NewsResponseToGetById?>?> GetNews(NewsRequestToGetAll request)
    {
        var news = await _newsRepository.GetAllNews();
        return news.Select(_mapper.Map<NewsResponseToGetById>);
    }

    public async Task<NewsResponseToGetById?> GetNewsById(NewsRequestToGetById request)
    {
        var news = await _newsRepository.GetNews(request.Id);
        return _mapper.Map<NewsResponseToGetById>(news);
    }

    public async Task<NewsResponseToGetById?> UpdateNews(NewsRequestToFullUpdate model)
    {
        var news = _mapper.Map<News>(model);
        Validate(news);
        news = await _newsRepository.UpdateNews(news);
        return _mapper.Map<NewsResponseToGetById>(news);
    }

    public async Task<NewsResponseToGetById?> DeleteNews(NewsRequestToDeleteById request)
    {
        var news = await _newsRepository.RemoveNews(request.Id);
        return _mapper.Map<NewsResponseToGetById>(news);
    }
    
    private bool Validate(News news)
    {
        var errors = new Dictionary<string, string[]>();
        if (news.Title.Length < 2 || news.Title.Length > 64)
        {
            errors.Add("Title",["Should be from 2 to 64 chars"]);
        }
        if (news.Content.Length < 4 || news.Content.Length > 2048)
        {
            errors.Add("Content",["Should be from 2 to 64 chars"]);
        }
        if (errors.Count != 0)
        {
            throw new BadRequestException("Validation error", errors);
        }
        return true;
    }
}