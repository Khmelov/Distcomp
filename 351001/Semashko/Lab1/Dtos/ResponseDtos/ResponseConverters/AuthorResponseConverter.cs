using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class AuthorResponseConverter:BaseResponse<AuthorResponseDto,Author> // Конвертер для преобразования доменного объекта Author в DTO ответа
    {
        // Преобразовать объект Author в AuthorResponseDto для отправки клиенту
        public AuthorResponseDto? ToDto(Author author) 
        {
            AuthorResponseDto? res = new AuthorResponseDto(); // Создать новый DTO
            res.firstname = author.Firstname; // Скопировать имя
            res.login = author.Login; // Скопировать логин
            res.lastname = author.Lastname; // Скопировать фамилию
            res.id = author.Id; // Скопировать ID
            res.password = author.Password; // Скопировать пароль (обычно не рекомендуется, но в коде так)
            return res; // Вернуть DTO
        }
    }
}
