using AutoMapper;
using Discussion.Common.Interface;
using Microsoft.AspNetCore.JsonPatch;
using Microsoft.AspNetCore.Mvc;
using System.Net;

namespace Discussion.Common
{
    public abstract class AbstractController<Entity, RequestTO, ResponseTO>
        (ICrudService<Entity, RequestTO, ResponseTO> Service, ILogger Logger, IMapper Mapper) : Controller
        where Entity : class
        where RequestTO : class
        where ResponseTO : class
    {
        [HttpGet]
        public virtual async Task<IActionResult> Read()
        {
            var entities = await Service.GetAll();

            Logger.LogInformation("Getting all {type}", typeof(Entity));

            var json = Json(entities);
            json.StatusCode = (int)HttpStatusCode.OK;
            return json;
        }

        [HttpPost]
        public virtual async Task<JsonResult> Create([FromBody] RequestTO request)
        {
            ResponseTO response = Mapper.Map<ResponseTO>(Mapper.Map<Entity>(request));
            var json = Json(response);
            json.StatusCode = (int)HttpStatusCode.Created;
            Logger.LogInformation("Creating {res}", Json(request).Value);

            try
            {
                response = await Service.Add(request);
                Logger.LogInformation("Creating {res}", response);
                json.Value = response;
            }
            catch (Exception ex)
            {
                Logger.LogError("Invalid request at ADD {type} {ex}", typeof(Entity), ex);
                json.StatusCode = (int)HttpStatusCode.Forbidden;
                json.Value = Json(new EmptyResult());
            }

            return json;
        }

        [HttpPut]
        public async Task<JsonResult> Update([FromBody] RequestTO request)
        {
            ResponseTO response = Mapper.Map<ResponseTO>(Mapper.Map<Entity>(request));
            var json = Json(response);
            json.StatusCode = (int)HttpStatusCode.OK;
            Logger.LogInformation("Updating {entity}: {request}", typeof(Entity), Json(request).Value);

            try
            {
                response = await Service.Update(request);
                json.Value = response;
            }
            catch (Exception ex)
            {
                Logger.LogError("Invalid request at UPDATE {type} {ex}", typeof(Entity), ex);
                json.StatusCode = (int)HttpStatusCode.BadRequest;
            }

            return json;
        }

        [HttpPatch]
        [Route("{id:int}")]
        public async Task<JsonResult> PartialUpdate([FromRoute] int id, [FromBody] JsonPatchDocument<Entity> patch)
        {
            JsonResult json = Json(patch);
            json.StatusCode = (int)HttpStatusCode.OK;
            Logger.LogInformation("Patching {author}", patch);

            try
            {
                var response = await Service.Patch(id, patch);
                json.Value = response;
            }
            catch (Exception ex)
            {
                Logger.LogError("Invalid request at PATCH {type} {ex}", typeof(Entity), ex);
                json.StatusCode = (int)HttpStatusCode.BadRequest;
            }

            return json;
        }

        [HttpDelete]
        [Route("{id:int}")]
        public async Task<IActionResult> Delete([FromRoute] int id)
        {
            bool res = false;
            Logger.LogInformation("Deleted {type} {id}", typeof(Entity), id);

            try
            {
                res = await Service.Remove(id);
            }
            catch
            {
                Logger.LogInformation("Deleting failed {id}", id);
            }

            return res ? NoContent() : BadRequest();
        }

        [HttpGet]
        [Route("{id:int}")]
        public async Task<JsonResult> GetByID([FromRoute] int id)
        {
            Logger.LogInformation("Get {type} {id}", typeof(Entity), id);
            var json = Json(id);
            json.StatusCode = (int)HttpStatusCode.OK;

            try
            {
                var response = await Service.GetByID(id);
                json.Value = response;
            }
            catch
            {
                Logger.LogError("ERROR getting {type} {id}", typeof(Entity), id);
                json.StatusCode = (int)HttpStatusCode.NotFound;
            }

            return json;
        }
    }
}
