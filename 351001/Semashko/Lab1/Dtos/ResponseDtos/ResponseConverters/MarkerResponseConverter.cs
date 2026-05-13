using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class MarkerResponseConverter : BaseResponse<MarkerResponseDto, Marker>
    {
        public MarkerResponseDto ToDto(Marker entity)
        {
            var dto = new MarkerResponseDto();
            dto.name = entity.Name;
            dto.id = entity.Id;
            return dto;
        }
    }
}
