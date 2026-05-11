
using FluentValidation;
using lab_1.Entities;
using Mapster;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{

    public class BaseRequest<T, TV> where T : TblBase
    {
        public T FromDto(TV dto)
        {
            
            return dto.Adapt<T>();
        }
    }
}
