/*namespace Task310ReatApi.Interfaces;

public class ICreatorService
{

}*/
// Interfaces/ICreatorService.cs
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;

namespace Task310RestApi.Interfaces
{
    public interface ICreatorService
    {
        Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync();
        Task<CreatorResponseTo?> GetCreatorByIdAsync(long id);
        Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest);
        Task<CreatorResponseTo?> UpdateCreatorAsync(long id, CreatorRequestTo creatorRequest);
        Task<bool> DeleteCreatorAsync(long id);
        Task<CreatorResponseTo?> GetCreatorByNewsIdAsync(long newsId);
    }
}