using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json;
using Npgsql;
using RV.Models;
using RV.Services.DataProviderServices;
using RV.Views;
using RV.Views.DTO;

namespace RV.Controllers
{
    [Route("api/v1.0/tweets")]
    [ApiController]
    public class TweetController : ControllerBase
    {
        private readonly IDataProvider _context;
        private readonly IDistributedCache _cache;

        public TweetController(IDataProvider context, IDistributedCache cache)
        {
            _context = context;
            _cache = cache;
        }

        [HttpGet]
        public IActionResult GetNews()
        {
            return StatusCode(200, _context.GetTweets());
        }

        [HttpGet("{id}")]
        public IActionResult GetNew(int id)
        {
            var res = _cache.GetString("news_" + id.ToString());
            if(res!= null)
                return StatusCode(200, JsonConvert.DeserializeObject<TweetDTO>(res));
            return StatusCode(200, _context.GetTweet(id));
        }

        [HttpPut]
        public IActionResult UpdateNews([FromBody] TweetUpdateDTO news)
        {
            try
            {
                var newNews = _context.UpdateTweet(news);
                _cache.SetString("news_" + newNews.id.ToString(), JsonConvert.SerializeObject(newNews), new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5)
                });
                return StatusCode(200, newNews);
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
        public IActionResult PostNews([FromBody] TweetAddDTO news)
        {
            try
            {
                var newNews = _context.CreateTweet(news);
                _cache.SetString("news_" + newNews.id.ToString(), JsonConvert.SerializeObject(newNews), new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5)
                });
                return StatusCode(201, newNews);
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
        public IActionResult DeleteNews(int id)
        {
            int res = _context.DeleteTweet(id);
            if (res == 0)
                return StatusCode(400);
            else { 
                _cache.Remove("news_" + id.ToString());
                return StatusCode(204);
            }
        }
    }
}
