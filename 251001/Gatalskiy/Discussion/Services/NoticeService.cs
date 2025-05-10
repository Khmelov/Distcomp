using AutoMapper;
using Discussion.DTOs;
using Discussion.Models;
using Discussion.Repositories;

namespace Discussion.Services;

public class NoticeService : INoticeService
{
    private readonly INoticeRepository _noticeRepository;
    private readonly IMapper _mapper;

    public NoticeService(INoticeRepository noticeRepository, IMapper mapper)
    {
        _noticeRepository = noticeRepository;
        _mapper = mapper;
    }

    public async Task<IEnumerable<NoticeResponseDTO>> GetAllNoticesAsync()
    {
        var notices = await _noticeRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<NoticeResponseDTO>>(notices);
    }

    public async Task<NoticeResponseDTO> GetNoticeByIdAsync(int id)
    {
        var notice = await _noticeRepository.GetByIdAsync(id);
        return _mapper.Map<NoticeResponseDTO>(notice);
    }

    public async Task<NoticeResponseDTO> CreateNoticeAsync(NoticeRequestDTO noticeCreateDto)
    {
        var notice = _mapper.Map<Notice>(noticeCreateDto);
        notice.CreatedAt = DateTime.UtcNow;
        var notices = GetAllNoticesAsync().Result;
        int id = 0;
        foreach (var noticeToCreate in notices)
            if (noticeToCreate.Id > id)
                id = noticeToCreate.Id;
        id++;
        notice.Id = id;
        await _noticeRepository.CreateAsync(notice);
        return _mapper.Map<NoticeResponseDTO>(notice);
    }

    public async Task<NoticeResponseDTO> UpdateNoticeAsync(int id, NoticeRequestDTO noticeUpdateDto)
    {
        var existingNotice = await _noticeRepository.GetByIdAsync(id);
        if (existingNotice == null)
        {
            throw new KeyNotFoundException($"Notice with id {id} not found.");
        }

        _mapper.Map(noticeUpdateDto, existingNotice);
        await _noticeRepository.UpdateAsync(id, existingNotice);
        var updatedNotice = await _noticeRepository.GetByIdAsync(id);
        return _mapper.Map<NoticeResponseDTO>(updatedNotice);
    }

    public async Task DeleteNoticeAsync(int id)
    {
        await _noticeRepository.DeleteAsync(id);
    }

}