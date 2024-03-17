using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;

namespace TaskREST.Services.Interfaces;

public interface ICreatorService
{
    Task<CreatorResponseTo> GetCreatorById(long id);
    Task<IEnumerable<CreatorResponseTo>> GetCreators();
    Task<CreatorResponseTo> CreateCreator(CreateCreatorRequestTo createCreatorRequestTo);
    Task DeleteCreator(long id);
    Task<CreatorResponseTo> UpdateCreator(UpdateCreatorRequestTo modifiedCreator);
}