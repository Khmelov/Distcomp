using Discussion.Common.Interface;
using Discussion.Storage.Cassandra;
using Microsoft.AspNetCore.JsonPatch;
using IMapper = Cassandra.Mapping.IMapper;

namespace Discussion.Common
{
    public abstract class AbstractCrudRepository<Entity>(CassandraStorage storage, Random random) : ICrudRepository<Entity>
        where Entity : AbstractEntity, new()
    {
        private readonly IMapper _mapper = storage.Mapper;

        public virtual Entity Add(Entity entity)
        {
            entity.Id = random.Next();
            _mapper.Insert(entity);

            return entity;
        }

        public virtual async Task<Entity> AddAsync(Entity entity)
        {
            entity.Id = random.Next();
            await _mapper.InsertAsync(entity);

            return entity;
        }

        public virtual bool DeleteById(int id)
        {
            var entity = _mapper.Fetch<Entity>().Where(x => x.Id == id).First();
            _mapper.Delete(entity);

            return true;
        }

        public virtual async Task<bool> DeleteByIdAsync(int id)
        {
            var entity = (await _mapper.FetchAsync<Entity>()).Where(x => x.Id == id).First();
            await _mapper.DeleteAsync(entity);

            return true;
        }

        public virtual IList<Entity> GetAll() => _mapper.Fetch<Entity>().ToList();

        public virtual async Task<IList<Entity>> GetAllAsync()
        {
            var dataset = await _mapper.FetchAsync<Entity>();

            return dataset.ToList();
        }

        public virtual Entity GetById(int id)
        {
            var query = _mapper.Fetch<Entity>().Where(x => x.Id == id);

            return query.First();
        }

        public virtual async Task<Entity> GetByIdAsync(int id)
        {
            var query = await _mapper.FetchAsync<Entity>();

            return query.Where(x => x.Id == id).First();
        }

        public virtual Entity Patch(int id, JsonPatchDocument<Entity> patch)
        {
            throw new NotImplementedException();
        }

        public virtual async Task<Entity> PatchAsync(int id, JsonPatchDocument<Entity> patch)
        {
            throw new NotImplementedException();
        }

        public virtual Entity Update(Entity entity)
        {
            _mapper.Update(entity);

            return entity;
        }

        public virtual async Task<Entity> UpdateAsync(Entity entity)
        {
            await _mapper.UpdateAsync(entity);

            return entity;
        }
    }
}
