namespace BlogService.Application.Interfaces.Mappers;

public interface IRequestMapper<RequestDto, Entity> where RequestDto : class where Entity : class
{
    Entity Map(RequestDto dto);
}