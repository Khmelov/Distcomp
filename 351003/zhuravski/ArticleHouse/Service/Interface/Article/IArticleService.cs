namespace ArticleHouse.Service.Interface.Article;

public interface IArticleService
{
    Task<ArticleResponseDTO[]> GetAllArticlesAsync();
    Task<ArticleResponseDTO> CreateArticleAsync(ArticleRequestDTO dto);
}