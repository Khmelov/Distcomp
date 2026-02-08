using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class ArticleService : IArticleService
{
    public async Task<ArticleResponseDTO[]> GetAllArticlesAsync()
    {
        return [];
    }

    public async Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto)
    {
        return default!;
    }
}