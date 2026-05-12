using lab_1.Domain;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/authors")]
    public class AuthorsController : ControllerBase
    {
        private readonly BaseService<AuthorRequestDto,AuthorResponseDto> _service;
        public AuthorsController(BaseService<AuthorRequestDto,AuthorResponseDto> authorService)
        {
            _service = authorService;
        }

        // Получить список всех авторов
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<AuthorResponseDto>> GetAuthors() => Ok(_service.GetAll());

        // Создать нового автора
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<AuthorResponseDto> CreateAuthor([FromBody]AuthorRequestDto dto)
        {
            var created = _service.Create(dto);
            return CreatedAtAction(nameof(GetAuthor), new { id = created?.id }, created);
        }

        // Удалить автора по ID
        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public ActionResult DeleteAuthor(long id)
        {
            return _service.Delete(id) ? NoContent() : NotFound();
        }

        // Обновить данные автора
        [HttpPut]
        public ActionResult<AuthorResponseDto> UpdateAuthor([FromBody] AuthorRequestDto dto)
        {
            var updated = _service.Update(dto);
            return updated == null ? NotFound() : Ok(updated);
        }

        // Получить автора по ID
        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<AuthorResponseDto> GetAuthor(long id)
        {
            var result = _service.Read(id);
            return result == null ? NotFound() : Ok(result);
        }
    }
}
/*{
"login":"asd",
"password":"asd",
"firstname":"asdf",
"lastname":"sdjkaf"
}*/

/*
REST API архитектурные принципы:

Client-Server
    Разделение ответственности между клиентом (UI) и сервером (данные/логика)
    Позволяет развивать компоненты независимо

Stateless
    Сервер не хранит состояние клиента между запросами
    Каждый запрос содержит всю информацию для его обработки
    Улучшает масштабируемость и надежность

Cacheable
    Ответы должны явно указывать возможность кеширования
    Снижает нагрузку на сервер и улучшает производительность
    Использует HTTP заголовки (Cache-Control, ETag)

Uniform Interface
    Единый способ взаимодействия через стандартные HTTP методы
    Ресурсы идентифицируются через URL
    Использование стандартных форматов данных (JSON/XML)

Layered System
    Архитектура может состоять из нескольких слоев (прокси, шлюзы)
    Каждый слой решает свою задачу
    Клиент взаимодействует только с ближайшим слоем
*/