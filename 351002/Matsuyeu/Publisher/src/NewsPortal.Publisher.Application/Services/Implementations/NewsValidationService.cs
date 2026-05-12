using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations
{
    public class NewsValidationService : INewsValidationService
    {
        private readonly IRepository<News> _newsRepository;

        public NewsValidationService(IRepository<News> newsRepository)
        {
            _newsRepository = newsRepository;
        }

        public async Task<bool> NewsExistsAsync(long newsId)
        {
            var news = await _newsRepository.GetByIdAsync(newsId);
            return news != null;
        }
    }
}
