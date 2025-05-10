using Discussion.DTOs;

namespace Discussion.Services;

public interface INoticeService
{
    Task<IEnumerable<NoticeResponseDTO>> GetAllNoticesAsync();
    Task<NoticeResponseDTO> GetNoticeByIdAsync(int id);
    Task<NoticeResponseDTO> CreateNoticeAsync(NoticeRequestDTO noticeCreateDto);
    Task<NoticeResponseDTO> UpdateNoticeAsync(int id, NoticeRequestDTO noticeUpdateDto);
    Task DeleteNoticeAsync(int id);
}