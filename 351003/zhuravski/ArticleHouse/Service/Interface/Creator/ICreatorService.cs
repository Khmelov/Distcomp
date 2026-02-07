namespace ArticleHouse.Service.CreatorService;

public interface ICreatorService
{
    Task<CreatorResponseDTO[]> GetAllCreatorsAsync();
    Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto);
}