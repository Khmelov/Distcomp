using WebApplication1.DTO;
using WebApplication1.Repository;

namespace WebApplication1.Service
{
    public interface ITagService
    {
        Task<TagResponseTo> CreateTagAsync(TagRequestTo dto);
        Task<TagResponseTo> GetTagByIdAsync(long id);
        Task<PaginatedResult<TagResponseTo>> GetAllTagsAsync(int pageNumber = 1, int pageSize = 10);
        Task<TagResponseTo> UpdateTagAsync(long id, TagRequestTo dto);
        Task DeleteTagAsync(long id);
    }
}
