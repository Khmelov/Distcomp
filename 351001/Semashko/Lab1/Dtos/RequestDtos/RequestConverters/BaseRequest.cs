using lab_1.Domain;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{
    public interface BaseRequest<T,V>
    {
        public T FromDto(V dto, long? id);
    }
}
