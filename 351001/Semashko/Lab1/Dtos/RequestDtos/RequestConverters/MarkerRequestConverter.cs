using lab_1.Domain;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{
    public class MarkerRequestConverter : BaseRequest<Marker, MarkerRequestDto>
    {
        public Marker FromDto(MarkerRequestDto dto, long? id)
        {
            return new Marker(id, dto.name);
        }
    }
}
