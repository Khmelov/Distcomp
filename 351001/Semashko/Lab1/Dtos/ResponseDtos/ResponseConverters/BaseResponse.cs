namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public interface BaseResponse<T,V>
    {
        public T ToDto(V entity);
    }
}
