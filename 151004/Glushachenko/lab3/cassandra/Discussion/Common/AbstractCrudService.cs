using AutoMapper;
using Discussion.Common.Interface;
using Microsoft.AspNetCore.JsonPatch;

namespace Discussion.Common
{
    public abstract class AbstractCrudService<Entity, RequestTo, ResponseTo>
        (IMapper mapper, ICrudRepository<Entity> repository)
        : ICrudService<Entity, RequestTo, ResponseTo>
        where Entity : class
    {
        public virtual ResponseTo AddSync(RequestTo requestTo)
        {
            var entity = mapper.Map<Entity>(requestTo);

            var entityResponse = repository.Add(entity);

            return mapper.Map<ResponseTo>(entityResponse);
        }

        public virtual async Task<ResponseTo> Add(RequestTo requestTo)
        {
            var entity = mapper.Map<Entity>(requestTo);

            var entityResponse = await repository.AddAsync(entity);

            return mapper.Map<ResponseTo>(entityResponse);
        }

        public virtual async Task<IList<ResponseTo>> GetAll()
        {
            var entities = await repository.GetAllAsync();

            return entities.Select(mapper.Map<ResponseTo>).ToList();
        }

        public virtual async Task<ResponseTo> GetByID(int id)
        {
            var response = await repository.GetByIdAsync(id);

            return response is not null ? mapper.Map<ResponseTo>(response)
                : throw new ArgumentNullException($"Not found {typeof(Entity)} {id}");
        }

        public virtual async Task<ResponseTo> Patch(int id, JsonPatchDocument<Entity> patch)
        {
            var response = await repository.PatchAsync(id, patch);

            return mapper.Map<ResponseTo>(response);
        }

        public virtual async Task<bool> Remove(int id) => await repository.DeleteByIdAsync(id);

        public virtual async Task<ResponseTo> Update(RequestTo request)
        {
            var entity = mapper.Map<Entity>(request);

            var authorResponse = await repository.UpdateAsync(entity);

            return mapper.Map<ResponseTo>(authorResponse);
        }
    }
}
