using Publisher.src.NewsPortal.Publisher.Application.Dtos.Auth;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions
{
    public interface ICreatorService
    {
        Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync();
        Task<CreatorResponseTo?> GetCreatorByIdAsync(long id);
        Task<CreatorResponseTo> GetCreatorByLoginAsync(string login);
        Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest);
        Task<CreatorResponseTo> RegisterAsync(RegisterRequestDto registerRequest);
        Task<LoginResponseDto> LoginAsync(LoginRequestDto loginRequest);
        Task<bool> UpdateCreatorAsync(CreatorRequestTo creatorRequest);
        Task<bool> DeleteCreatorAsync(long id);
        Task<PagedResult<CreatorResponseTo>> GetPagedCreatorsAsync(QueryParameters parameters);
    }
}
