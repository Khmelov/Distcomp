using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using Publisher.Controllers.V1_0.Common;
using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Service.Interface;
using System.Net;

namespace Publisher.Controllers.V1_0
{
    [Route("api/v1.0/authors")]
    [ApiController]
    public class AuthorController(IAuthorService AuthorService, ILogger<AuthorController> Logger, IMapper Mapper) :
        AbstractController<Author, AuthorRequestTO, AuthorResponseTO>(AuthorService, Logger, Mapper)
    {
        [HttpGet]
        [Route("tweets/{id:int}")]
        public async Task<JsonResult> GetByTweetID([FromRoute] int id)
        {
            AuthorResponseTO? response = null;
            Logger.LogInformation("Getting author by tweet ID: {id}", id);

            try
            {
                response = await AuthorService.GetByTweetID(id);
            }
            catch (Exception ex)
            {
                Logger.LogError("Error getting author by tweet ID {ex}", ex);
                Response.StatusCode = (int)HttpStatusCode.BadRequest;
            }

            return Json(response);
        }
    }
}
