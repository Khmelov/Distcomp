namespace DC_REST.Repositories
{
	public class InMemoryRepository<TEntity>: IRepository<TEntity>
	{
		private readonly Dictionary<int, TEntity> _data = new Dictionary<int, TEntity>();
		private int _currentId = 0;

		public TEntity GetById(int id)
		{
			return _data.ContainsKey(id) ? _data[id] : default;
		}

		public List<TEntity> GetAll()
		{
			return _data.Values.ToList();
		}

		public TEntity Add(TEntity entity)
		{
			
			_data.Add(_currentId, entity);
			_currentId++;
			//_data[id] = entity;
			return entity;
		}
		public TEntity Update(int id, TEntity entity)
		{
			if (_data.ContainsKey(id))
			{
				_data[id] = entity;
				return entity;
			}

			return default;
		}

		public bool Delete(int id)
		{
			//_currentId--;
			return _data.Remove(id);
		}
		public int GetCurrentId()
		{
			return _currentId;
		}
	}
}
