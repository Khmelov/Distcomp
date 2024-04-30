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
    [Route("api/v1.0/users")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly IDataProvider _context;
        private readonly IDistributedCache _cache;

        public UsersController(IDataProvider context, IDistributedCache cache)
        {
            _context = context;
            _cache = cache;
        }

        [HttpGet]
        public IActionResult GetUsers()
        {
            return StatusCode(200, _context.GetUsers());
        }

        [HttpGet("{id}")]
        public IActionResult GetUser(int id)
        {
            var res = _cache.GetString("user_" + id.ToString());
            if (res != null)
                return StatusCode(200, JsonConvert.DeserializeObject<UserDTO>(res));
            return StatusCode(200, _context.GetUser(id));
        }

        [HttpPut]
        public IActionResult UpdateUser([FromBody]UserUpdateDTO user)
        {
            try
            {
                var newUser = _context.UpdateUser(user);
                _cache.SetString("user_" + newUser.id.ToString(), JsonConvert.SerializeObject(newUser), new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5)
                });
                return StatusCode(200, newUser);
            } catch (DbUpdateException ex) {
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
        public IActionResult PostUser([FromBody]UserAddDTO user)
        {
            try
            {
                return StatusCode(201, _context.CreateUser(user));
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
        public IActionResult DeleteUser(int id)
        {
            int res = _context.DeleteUser(id);
            if (res == 0)
                return StatusCode(400);
            else
            {
                _cache.Remove("user_" + id.ToString());
                return StatusCode(204);
            }
        }
    }
}
