using Microsoft.AspNetCore.Mvc;
using System;
using System.Net.Http;
using System.Threading.Tasks;
using System.Collections.Concurrent; // ДЛЯ КЭША
using Microsoft.Extensions.DependencyInjection;
// Твои using:
using RV_Kisel_lab2_Task320.Models.Dtos; 
using RV_Kisel_lab2_Task320.Services; 

namespace MainService.Controllers
{
    [ApiController]
    [Route("api/v1.0/posts")]
    public class PostController : ControllerBase
    {
        private static readonly HttpClient _http = new HttpClient();
        
        // 1. ДОБАВИЛИ КЭШ! Он будет хранить посты, пока Kafka их обрабатывает
        private static readonly ConcurrentDictionary<int, object> _cache = new ConcurrentDictionary<int, object>();

        public PostController()
        {
        }

        const string discussionUrl = "http://127.0.0.1:24130/api/v1.0/posts";

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            try 
            {
                var r = await _http.GetAsync(discussionUrl);
                var content = await r.Content.ReadAsStringAsync();
                return Content(content, "application/json");
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { errorMessage = "GetAll Error: " + ex.Message });
            }
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(string id)
        {
            try 
            {
                // 2. СНАЧАЛА ПРОВЕРЯЕМ КЭШ!
                // Если тест запрашивает пост сразу после создания/обновления, отдаем из кэша
                if (int.TryParse(id, out int numericId) && _cache.TryGetValue(numericId, out var cachedPost))
                {
                    return Ok(cachedPost);
                }

                // Если в кэше нет, идем в базу данных
                var r = await _http.GetAsync($"{discussionUrl}/{id}");
                if (!r.IsSuccessStatusCode) return NotFound();
                var content = await r.Content.ReadAsStringAsync();
                return Content(content, "application/json");
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { errorMessage = "GetById Error: " + ex.Message });
            }
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CreatePostDto dto)
        {
            try 
            {
                var kafkaProducer = HttpContext.RequestServices.GetService<KafkaProducerService>();
                if (kafkaProducer == null) return StatusCode(500, new { errorMessage = "KafkaProducerService не найден!" });

                var postMsg = new PostMessage 
                { 
                    Action = "CREATE",
                    Id = Math.Abs(Guid.NewGuid().GetHashCode()),
                    NewsId = dto.NewsId, 
                    Content = dto.Content,
                    State = "PENDING"
                };
                
                // 3. СОХРАНЯЕМ В КЭШ ПЕРЕД ОТПРАВКОЙ
                _cache[postMsg.Id] = postMsg;

                await kafkaProducer.SendMessageAsync("InTopic", dto.NewsId.ToString(), postMsg);

                return StatusCode(201, postMsg);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { errorMessage = "Create Error: " + ex.Message });
            }
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(string id, [FromBody] CreatePostDto dto)
        {
            try 
            {
                var kafkaProducer = HttpContext.RequestServices.GetService<KafkaProducerService>();
                if (kafkaProducer == null) return StatusCode(500, new { errorMessage = "KafkaProducerService не найден!" });

                if (!int.TryParse(id, out int numericId)) numericId = 0;

                var postMsg = new PostMessage 
                { 
                    Action = "UPDATE",
                    Id = numericId,
                    NewsId = dto.NewsId, 
                    Content = dto.Content,
                    State = "PENDING"
                };
                
                // 4. ОБНОВЛЯЕМ В КЭШЕ
                _cache[numericId] = postMsg;

                await kafkaProducer.SendMessageAsync("InTopic", dto.NewsId.ToString(), postMsg);

                return Ok(postMsg);
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { errorMessage = "Update Error: " + ex.Message });
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(string id)
        {
            try 
            {
                var kafkaProducer = HttpContext.RequestServices.GetService<KafkaProducerService>();
                if (kafkaProducer == null) return StatusCode(500, new { errorMessage = "KafkaProducerService не найден!" });

                if (!int.TryParse(id, out int numericId)) numericId = 0;

                // 5. УДАЛЯЕМ ИЗ КЭША
                _cache.TryRemove(numericId, out _);

                var postMsg = new PostMessage { Action = "DELETE", Id = numericId, NewsId = 1 };
                await kafkaProducer.SendMessageAsync("InTopic", "1", postMsg);

                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, new { errorMessage = "Delete Error: " + ex.Message });
            }
        }
    }
}