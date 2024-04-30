using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json;
using Npgsql;
using RV.Models;
using RV.Services.DataProviderServices;
using RV.Views.DTO;

namespace RV.Controllers
{
    [Route("api/v1.0/stickers")]
    [ApiController]
    public class StickersController : ControllerBase
    {
        private readonly IDataProvider _context;
        private readonly IDistributedCache _cache;

        public StickersController(IDataProvider context, IDistributedCache cache)
        {
            _context = context;
            _cache = cache;
        }

        [HttpGet]
        public IActionResult GetStickers()
        {
            return StatusCode(200, _context.GetStickers());
        }

        [HttpGet("{id}")]
        public IActionResult GetSticker(int id)
        {
            var res = _cache.GetString("sticker_" + id.ToString());
            if (res != null)
                return StatusCode(200, JsonConvert.DeserializeObject<StickerDTO>(res));
            return StatusCode(200, _context.GetSticker(id));
        }

        [HttpPut]
        public IActionResult UpdateSticker([FromBody] StickerUpdateDTO sticker)
        {
            try
            {
                var newSticker = _context.UpdateSticker(sticker);
                _cache.SetString("sticker_" + newSticker.id.ToString(), JsonConvert.SerializeObject(newSticker), new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5)
                });
                return StatusCode(200, newSticker);
            }
            catch (DbUpdateException ex)
            {
                string errorCode = "400";
                string hash = ex.Message.GetHashCode().ToString();
                errorCode += hash[0];
                errorCode += hash[1];
                Dictionary<string, string> error = new Dictionary<string, string>() {
                    { "errorMeassage", ex.Message},
                    { "errorCode", errorCode}
                };
                return StatusCode(400, error);
            }
        }

        [HttpPost]
        public IActionResult PostSticker([FromBody] StickerAddDTO sticker)
        {
            try { 
                return StatusCode(201, _context.CreateSticker(sticker));
            }
            catch (DbUpdateException ex)
            {
                int statusCode = 400;
                if (ex.InnerException is PostgresException postgresException)
                {
                    if (postgresException.SqlState == "23505")
                    {
                        statusCode = 403;
                    }
                }
                string hash = ex.Message.GetHashCode().ToString();
                string errorCode = statusCode.ToString();
                errorCode += hash[0];
                errorCode += hash[1];
                Dictionary<string, string> error = new Dictionary<string, string>() {
                    { "errorMeassage", ex.Message},
                    { "errorCode", errorCode}
                };
                return StatusCode(statusCode, error);
            }
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteSticker(int id)
        {
            int res = _context.DeleteSticker(id);
            if (res == 0)
                return StatusCode(400);
            else
            {
                _cache.Remove("sticker_" + id.ToString());
                return StatusCode(204);
            }
        }
    }
}
