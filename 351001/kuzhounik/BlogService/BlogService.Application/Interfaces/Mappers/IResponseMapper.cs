namespace BlogService.Application.Interfaces.Mappers;

public interface IResponseMapper<Entity, ResponseDto> where Entity : class where ResponseDto : class
{
    ResponseDto Map(Entity entity);
}