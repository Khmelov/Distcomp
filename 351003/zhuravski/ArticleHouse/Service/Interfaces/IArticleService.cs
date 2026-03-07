using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface IArticleService
{
    Task<ArticleResponseDTO[]> GetAllArticlesAsync();
    Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto);
    Task<ArticleResponseDTO> GetArticleByIdAsync(long id);
    Task DeleteArticleAsync(long id);
    Task<ArticleResponseDTO> UpdateArticleByIdAsync(long id, ArticleRequestDTO dto);
}