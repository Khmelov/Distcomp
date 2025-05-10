using WebApplication1.DTO;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public interface ITopicService
    {
        Task<TopicResponseTo> CreateTopicAsync(TopicRequestTo dto);
        Task<TopicResponseTo> GetTopicByIdAsync(long id);
        Task<PaginatedResult<TopicResponseTo>> GetAllTopicsAsync(
            int pageNumber = 1,
            int pageSize = 10,
            string? sortBy = null,
            string? filter = null);
        Task<TopicResponseTo> UpdateTopicAsync(long id, TopicRequestTo dto);
        Task DeleteTopicAsync(long id);
    }
}
