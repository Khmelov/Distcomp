using distcomp.DTOs;
using distcomp.Models;
using distcomp.Repositories;

namespace distcomp.Services
{
    public class ArticleService
    {

        private readonly ArticleRepository _articleRepository;

        public ArticleService(ArticleRepository articleRepository)
        {
            _articleRepository = articleRepository;
        }

        
        public ArticleResponseTo CreateArticle(ArticleRequestTo articleRequest)
        {
            var article = new Article
            {
                CreatorId = articleRequest.CreatorId,
                Title = articleRequest.Title,
                Content = articleRequest.Content,
                Created = DateTime.UtcNow,
                Modified = DateTime.UtcNow
            };
            var savedArticle = _articleRepository.Save(article);
            return MapToArticleResponse(savedArticle);
        }

        
        public ArticleResponseTo GetArticleById(long id)
        {
            var article = _articleRepository.FindById(id);
            return article != null ? MapToArticleResponse(article) : null;
        }

        
        public List<ArticleResponseTo> GetAllArticles()
        {
            var articles = _articleRepository.FindAll();
            var articleResponses = new List<ArticleResponseTo>();

            foreach (var article in articles)
            {
                articleResponses.Add(MapToArticleResponse(article));
            }
            return articleResponses;
        }

       
        public ArticleResponseTo UpdateArticle(long id, ArticleRequestTo articleRequest)
        {
            var article = _articleRepository.FindById(id);
            if (article == null)
                return null;

            article.CreatorId = articleRequest.CreatorId;
            article.Title = articleRequest.Title;
            article.Content = articleRequest.Content;
            article.Modified = DateTime.UtcNow;



            var updatedArticle = _articleRepository.Update(article);
            return MapToArticleResponse(updatedArticle);
        }

        
        public bool DeleteArticle(long id)
        {
            return _articleRepository.DeleteById(id);
        }

        //transforming topic into dto
        private ArticleResponseTo MapToArticleResponse(Article article)
        {
            return new ArticleResponseTo
            {
                Id = article.Id,
                CreatorId = article.CreatorId,
                Title = article.Title,
                Content = article.Content,
                Created = article.Created,
                Modified = article.Modified
            };
        }

    }
}
