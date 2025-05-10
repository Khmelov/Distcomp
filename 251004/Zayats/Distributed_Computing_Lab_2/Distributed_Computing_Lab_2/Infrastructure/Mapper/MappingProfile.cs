using AutoMapper;
using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.DTO.ResponseDTO;
using Distributed_Computing_Lab_2.Models;

namespace Distributed_Computing_Lab_2.Infrastructure.Mapper;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<Author, AuthorResponseDTO>();
        CreateMap<AuthorRequestDTO, Author>();

        CreateMap<Message, MessageResponseDTO>();
        CreateMap<MessageRequestDTO, Message>();

        CreateMap<Sticker, StickerResponseDTO>();
        CreateMap<StickerRequestDTO, Sticker>();

        CreateMap<Topic, TopicResponseDTO>();
        CreateMap<TopicRequestDTO, Topic>();
    }
}