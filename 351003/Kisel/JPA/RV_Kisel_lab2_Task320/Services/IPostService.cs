using RV_Kisel_lab2_Task320.Models.Dtos;

namespace RV_Kisel_lab2_Task320.Services;

public interface IPostService
{
    Task<IEnumerable<PostDto>> GetAllAsync();
    Task<PostDto?> GetByIdAsync(int id);
    Task<PostDto> CreateAsync(PostDto dto);
    Task UpdateAsync(int id, PostDto dto);
    Task DeleteAsync(int id);
}