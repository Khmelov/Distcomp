using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface IArticleService
{
    Task<ArticleResponseDTO[]> GetAllArticlesAsync();
    Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto);
}