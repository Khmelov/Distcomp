using RV_Kisel_lab2_Task320.Models.Dtos;

namespace RV_Kisel_lab2_Task320.Services;

public interface ICreatorService {
    Task<IEnumerable<CreatorDto>> GetAllAsync();
    Task<CreatorDto?> GetByIdAsync(int id);
    Task<CreatorDto> CreateAsync(CreatorDto dto);
    Task UpdateAsync(int id, CreatorDto dto); // Добавлено!
    Task DeleteAsync(int id);
}