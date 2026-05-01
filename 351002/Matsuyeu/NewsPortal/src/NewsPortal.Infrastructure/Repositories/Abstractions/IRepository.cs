// Models/Repositories/Abstractions/IRepository.cs
using System.Linq.Expressions;

namespace NewsPortal.Models.Repositories.Abstractions
{
    /// <summary>
    /// Параметры запроса для пагинации, фильтрации и сортировки
    /// </summary>
    public class QueryParameters
    {
        private const int MaxPageSize = 100;
        private int _pageSize = 10;

        /// <summary>
        /// Номер страницы (начиная с 1)
        /// </summary>
        public int PageNumber { get; set; } = 1;

        /// <summary>
        /// Размер страницы (макс. 100)
        /// </summary>
        public int PageSize
        {
            get => _pageSize;
            set => _pageSize = value > MaxPageSize ? MaxPageSize : value;
        }

        /// <summary>
        /// Поле для сортировки
        /// </summary>
        public string? SortBy { get; set; }

        /// <summary>
        /// Направление сортировки (asc/desc)
        /// </summary>
        public string? SortOrder { get; set; } = "asc";

        /// <summary>
        /// Поисковый запрос
        /// </summary>
        public string? SearchTerm { get; set; }

        /// <summary>
        /// Фильтр по дате (для News)
        /// </summary>
        public DateTime? FromDate { get; set; }

        /// <summary>
        /// Фильтр по дате (для News)
        /// </summary>
        public DateTime? ToDate { get; set; }
    }

    /// <summary>
    /// Результат запроса с пагинацией
    /// </summary>
    /// <typeparam name="T">Тип сущности</typeparam>
    public class PagedResult<T>
    {
        public IEnumerable<T> Items { get; set; } = new List<T>();
        public int TotalCount { get; set; }
        public int PageNumber { get; set; }
        public int PageSize { get; set; }
        public int TotalPages => (int)Math.Ceiling(TotalCount / (double)PageSize);
        public bool HasPrevious => PageNumber > 1;
        public bool HasNext => PageNumber < TotalPages;
    }

    /// <summary>
    /// Обобщенный интерфейс репозитория с поддержкой CRUD и расширенного поиска
    /// </summary>
    /// <typeparam name="T">Тип сущности</typeparam>
    public interface IRepository<T> where T : class
    {
        // Базовые CRUD операции
        Task<IEnumerable<T>> GetAllAsync();
        Task<T?> GetByIdAsync(long id);
        Task<T> AddAsync(T entity);
        Task UpdateAsync(T entity);
        Task DeleteAsync(long id);
        Task<bool> ExistsAsync(long id);

        // Операции с пагинацией и фильтрацией
        Task<PagedResult<T>> GetPagedAsync(QueryParameters parameters);
        Task<IEnumerable<T>> FindAsync(Expression<Func<T, bool>> predicate);
        Task<T?> FindSingleAsync(Expression<Func<T, bool>> predicate);

        // Подсчет
        Task<int> CountAsync(Expression<Func<T, bool>>? predicate = null);
    }
}