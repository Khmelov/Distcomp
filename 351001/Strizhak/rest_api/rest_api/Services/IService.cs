namespace rest_api.Services
{
    /// <summary>
    /// Базовый интерфейс для сервисов, работающих с сущностями.
    /// </summary>
    /// <typeparam name="T">Тип сущности (например, User, Topic)</typeparam>
    /// <typeparam name="TRequest">Тип DTO для создания/обновления</typeparam>
    /// <typeparam name="TResponse">Тип DTO для ответа</typeparam>
    public interface IService<T, TRequest, TResponse>
        where T : class
    {
        TResponse? GetById(long id);
        IEnumerable<TResponse> GetAll();
        TResponse Create(TRequest request);
        TResponse Update(TRequest request);
        void Delete(long id);
    }
}