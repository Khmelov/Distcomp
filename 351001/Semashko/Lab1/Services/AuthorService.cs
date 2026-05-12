using System.ComponentModel.DataAnnotations;
using lab_1.Domain;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Repositories;

namespace lab_1.Services
{
    public class AuthorService : BaseService<AuthorRequestDto, AuthorResponseDto>
    {

        Repository<Author> authors; // Репозиторий для хранения авторов в памяти
        AuthorRequestConverter authorRequest; // Конвертер для преобразования DTO запроса в доменный объект
        AuthorResponseConverter authorResponse; // Конвертер для преобразования доменного объекта в DTO ответа
        ListAuthorResponseConverter converter; // Конвертер для списка (не используется в коде, но инициализирован)
        public AuthorService() 
        {
            authors = new Repository<Author>(); // Инициализация репозитория
            authorRequest = new AuthorRequestConverter(); // Инициализация конвертера запроса
            authorResponse = new AuthorResponseConverter(); // Инициализация конвертера ответа
            converter = new ListAuthorResponseConverter(); // Инициализация конвертера списка
        }
        // Создать нового автора: преобразовать DTO в объект, добавить в репозиторий, вернуть DTO ответа
        public AuthorResponseDto? Create(AuthorRequestDto dto)
        {
           authors.AddValue(authorRequest.FromDto(dto, authors.NextId)); // Добавить в репозиторий с новым ID
           return authorResponse.ToDto(authors.FindById(authors.NextId-1)); // Вернуть DTO созданного автора
        }

        // Удалить автора по ID: вызвать удаление в репозитории
        public bool Delete(long id)
        {
            return authors.DeleteValue(id); // Вернуть результат удаления
        }

        // Прочитать автора по ID: найти в репозитории и преобразовать в DTO
        public AuthorResponseDto? Read(long id)=>authorResponse.ToDto(authors.FindById(id));
        

        // Обновить автора: проверить ID, валидировать, обновить в репозитории, вернуть DTO
        public AuthorResponseDto? Update(AuthorRequestDto dto)
        {
            if (dto.id == null || !authors.Contains(dto.id)) // Проверить, существует ли автор
                return null;

            ICollection<ValidationResult> results = new List<ValidationResult>(); // Для результатов валидации
            var test = authorRequest.FromDto(dto, dto.id); // Преобразовать DTO в объект
            if (Validator.TryValidateObject(test, new ValidationContext(test), results, validateAllProperties: true)) // Валидировать объект
            {
                authors.UpdateValue(test, dto.id); // Обновить в репозитории
                return authorResponse.ToDto(authors.FindById(dto.id)); // Вернуть обновленный DTO
            }
            return null;
        }

        public List<AuthorResponseDto?> GetAll() => converter.AuthorsResponse(authors.GetAuthors()).ToList();
    }
}
