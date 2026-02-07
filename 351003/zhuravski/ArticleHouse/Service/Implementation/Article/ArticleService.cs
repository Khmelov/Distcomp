using ArticleHouse.Service.Interface.Article;

namespace ArticleHouse.Service.Implementation.Article;

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