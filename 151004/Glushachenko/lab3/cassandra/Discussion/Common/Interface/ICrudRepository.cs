using Microsoft.AspNetCore.JsonPatch;

namespace Discussion.Common.Interface
{
    public interface ICrudRepository<Entity> where Entity : class
    {
        IList<Entity> GetAll();
        Task<IList<Entity>> GetAllAsync();
        Entity GetById(int id);
        Task<Entity> GetByIdAsync(int id);
        Entity Add(Entity entity);
        Task<Entity> AddAsync(Entity entity);
        Entity Update(Entity entity);
        Task<Entity> UpdateAsync(Entity entity);
        bool DeleteById(int id);
        Task<bool> DeleteByIdAsync(int id);
        Entity Patch(int id, JsonPatchDocument<Entity> patch);
        Task<Entity> PatchAsync(int id, JsonPatchDocument<Entity> patch);
    }
}
