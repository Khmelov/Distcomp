using ArticleHouse.Service.DTOs;

namespace ArticleHouse.Service.Interfaces;

public interface IMarkService
{
    Task<MarkResponseDTO[]> GetAllMarksAsync();
    Task<MarkResponseDTO> CreateMarkAsync(MarkRequestDTO dto);
    Task DeleteMarkAsync(long id);
    Task<MarkResponseDTO> GetMarkByIdAsync(long id);
    Task<MarkResponseDTO> UpdateMarkByIdAsync(long id, MarkRequestDTO dto);
}