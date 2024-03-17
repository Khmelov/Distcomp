using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;

namespace TaskREST.Services.Interfaces;

public interface ITagService
{
    Task<TagResponseTo> GetTagById(long id);
    Task<IEnumerable<TagResponseTo>> GetTags();
    Task<TagResponseTo> CreateTag(CreateTagRequestTo createTagRequestTo);
    Task DeleteTag(long id);
    Task<TagResponseTo> UpdateTag(UpdateTagRequestTo modifiedTag);
}