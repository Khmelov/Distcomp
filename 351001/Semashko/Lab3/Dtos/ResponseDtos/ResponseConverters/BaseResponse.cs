using lab_1.Entities;
using Mapster;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class BaseResponse<T,V> where V:TblBase
    {
        public T ToDto(V entity) => entity.Adapt<T>();
    }
}
