using Microsoft.AspNetCore.JsonPatch;
using Microsoft.EntityFrameworkCore;
using Publisher.Entity.Common;
using Publisher.Repository.Interface.Common;
using Publisher.Storage.Common;

namespace Publisher.Repository.Implementation.Common
{
    public abstract class AbstractCrudRepository<Entity>(DbStorage storage) : ICrudRepository<Entity>
        where Entity : AbstractEntity, new()
    {
        protected DbSet<Entity> _entitySet = storage.Set<Entity>();

        public virtual Entity Add(Entity entity)
        {
            _entitySet.Add(entity);
            storage.SaveChanges();
            return entity;
        }

        public virtual async Task<Entity> AddAsync(Entity entity)
        {
            await _entitySet.AddAsync(entity);
            await storage.SaveChangesAsync();
            return entity;
        }

        public virtual bool DeleteById(int id)
        {
            _entitySet.Remove(new Entity { Id = id });
            var rowsAffected = storage.SaveChanges();
            return rowsAffected != 0;
        }

        public virtual async Task<bool> DeleteByIdAsync(int id)
        {
            _entitySet.Remove(new Entity { Id = id });
            var rowsAffected = await storage.SaveChangesAsync();
            return rowsAffected != 0;
        }

        public virtual IList<Entity> GetAll() => [.. _entitySet];

        public virtual async Task<IList<Entity>> GetAllAsync() => await _entitySet.ToListAsync();

        public virtual Entity GetById(int id) => 
            _entitySet.Find(id) ?? throw new ArgumentNullException($"{typeof(Entity)} not found: {id}");

        public virtual async Task<Entity> GetByIdAsync(int id) =>
            await _entitySet.FindAsync(id) ?? throw new ArgumentNullException($"{typeof(Entity)} not found: {id}");

        public virtual Entity Patch(int id, JsonPatchDocument<Entity> patch)
        {
            var entity = _entitySet.Find(id)
                ?? throw new InvalidDataException($"{typeof(Entity)} {id} not found at PATCH {patch}");

            patch.ApplyTo(entity);
            storage.SaveChanges();

            return entity;
        }

        public virtual async Task<Entity> PatchAsync(int id, JsonPatchDocument<Entity> patch)
        {
            var entity = await _entitySet.FindAsync(id)
                ?? throw new InvalidDataException($"{typeof(Entity)} {id} not found at PATCH {patch}");

            patch.ApplyTo(entity);
            await storage.SaveChangesAsync();

            return entity;
        }

        public virtual Entity Update(Entity entity)
        {
            _entitySet.Update(entity);
            storage.SaveChanges();
            return entity;
        }

        public virtual async Task<Entity> UpdateAsync(Entity entity)
        {
            _entitySet.Update(entity);
            await storage.SaveChangesAsync();
            return entity;
        }
    }
}
