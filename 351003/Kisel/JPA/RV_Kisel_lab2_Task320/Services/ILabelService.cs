using RV_Kisel_lab2_Task320.Models.Dtos;

namespace RV_Kisel_lab2_Task320.Services;

public interface ILabelService
{
    Task<IEnumerable<LabelDto>> GetAllAsync();
    Task<LabelDto?> GetByIdAsync(int id);
    Task<LabelDto> CreateAsync(LabelDto dto);
    Task UpdateAsync(int id, LabelDto dto);
    Task DeleteAsync(int id);
}