using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface ICreatorService
{
    Task<CreatorResponseDTO[]> GetAllCreatorsAsync();
    Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto);
    Task DeleteCreatorAsync(long creatorId);
    Task<CreatorResponseDTO> GetCreatorByIdAsync(long creatorId);
    Task<CreatorResponseDTO> UpdateCreatorByIdAsync(long creatorId, CreatorRequestDTO dto);
}