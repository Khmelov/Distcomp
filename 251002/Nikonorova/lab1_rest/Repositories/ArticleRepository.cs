using distcomp.Models;
using System.Collections.Generic;
using System.Linq;

namespace distcomp.Repositories
{
    public class ArticleRepository : ICrudRepository<Article, long>
    {
        private readonly Dictionary<long, Article> _articles = new();
        private long _idCounter = 0;

        public Article Save(Article article)
        {
            article.Id = ++_idCounter;
            _articles[article.Id] = article;
            return article;
        }



        public Article FindById(long id)
        {
            return _articles.TryGetValue(id, out var article) ? article : null;
        }

        public List<Article> FindAll()
        {
            return _articles.Values.ToList();
        }

        public Article Update(Article article)
        {
            if (_articles.ContainsKey(article.Id))
            {
                _articles[article.Id] = article; 
                return article;
            }
            return null; 
        }

        public bool DeleteById(long id)
        {
            return _articles.Remove(id); 
        }
    }
}
