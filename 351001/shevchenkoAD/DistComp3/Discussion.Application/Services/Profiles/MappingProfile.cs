using AutoMapper;
using Discussion.Application.DTOs.Requests;
using Discussion.Application.DTOs.Responses;
using Discussion.Domain.Entities;

namespace Discussion.Application.Services.Profiles;

public class MappingProfile : Profile {
    public MappingProfile() {
        CreateMap<CommentRequestTo, Comment>();
        CreateMap<Comment, CommentResponseTo>();
    }
}