using RV_Kisel_lab2_Task320.Models.Dtos;

namespace RV_Kisel_lab2_Task320.Services
{
    public interface INewsService
    {
        Task<IEnumerable<NewsDto>> GetAllAsync();
        // Теперь компилятор найдет NewsResponseDto, т.к. он в том же namespace, что и NewsDto
        Task<NewsResponseDto?> GetByIdAsync(int id); 
        Task<NewsDto> CreateAsync(NewsDto dto);
        Task UpdateAsync(int id, NewsDto dto);
        Task DeleteAsync(int id);
        Task<bool> ExistsByTitleAsync(string title);
    }
}