using AutoMapper;
using Discussion.DTOs;
using Discussion.Models;

public class NoticeProfile : Profile
{
    public NoticeProfile()
    {
        // Маппинг из модели в DTO ответа
        CreateMap<Notice, NoticeResponseDTO>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
            .ForMember(dest => dest.Content, opt => opt.MapFrom(src => src.Content))
            .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId));

        // Маппинг из DTO запроса в модель
        CreateMap<NoticeRequestDTO, Notice>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
            .ForMember(dest => dest.Content, opt => opt.MapFrom(src => src.Content))
            .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId.ToString()))
            .ForMember(dest => dest.CreatedAt, opt => opt.MapFrom(_ => DateTime.UtcNow));

        // Маппинг для обновления (используем NoticeRequestDTO как DTO для обновления)
        CreateMap<NoticeRequestDTO, Notice>()
            .ForMember(dest => dest.Id, opt => opt.MapFrom(src => src.Id))
            .ForMember(dest => dest.Content, opt => opt.MapFrom(src => src.Content))
            .ForMember(dest => dest.NewsId, opt => opt.MapFrom(src => src.NewsId.ToString()))
            .ForMember(dest => dest.CreatedAt, opt => opt.Ignore());
    }
}