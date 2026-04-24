using BusinessLogic.Repository;
using Cassandra;
using Cassandra.Mapping;
using Cassandra.Mapping.Attributes;

namespace Infrastructure.RepositoryImplementation
{
    public class CassandraRepository<TEntity> : IRepository<TEntity> where TEntity : class
    {
        private readonly IMapper _mapper;
        private readonly string _keyspace;

        public CassandraRepository(ISession session)
        {
            _mapper = new Mapper(session);
            _keyspace = session.Keyspace; 
        }

        public async Task<TEntity> GetByIdAsync(int id)
        {
            return await _mapper.FirstOrDefaultAsync<TEntity>("WHERE id = ?", id);
        }

        public async Task<List<TEntity>> GetAllAsync()
        {
            var tableName = GetTableName();
            var result = await _mapper.FetchAsync<TEntity>($"SELECT * FROM {_keyspace}.{tableName}");
            return result.ToList();
        }

        public async Task<TEntity> CreateAsync(TEntity entity)
        {
            await _mapper.InsertAsync(entity);
            return entity;
        }

        public async Task<TEntity> UpdateAsync(TEntity entity)
        {
            await _mapper.UpdateAsync(entity);
            return entity;
        }

        public async Task DeleteAsync(int id)
        {
            await _mapper.DeleteAsync<TEntity>("WHERE id = ?", id);
        }

        public async Task<bool> ExistsAsync(int id)
        {
            var result = await _mapper.FirstOrDefaultAsync<TEntity>("WHERE id = ?", id);
            return result != null;
        }

        private string GetTableName()
        {
            var tableAttr = typeof(TEntity).GetCustomAttributes(typeof(TableAttribute), false)
                .FirstOrDefault() as TableAttribute;

            if (tableAttr == null)
                throw new InvalidOperationException($"Table attribute not defined for entity {typeof(TEntity).Name}");

            return tableAttr.Name;
        }
    }
}