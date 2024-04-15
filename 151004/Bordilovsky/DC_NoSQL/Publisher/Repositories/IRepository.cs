namespace DC_REST.Repositories
{
	public interface IRepository<TEntity>
	{
		TEntity GetById(int id);
		List <TEntity> GetAll();
		TEntity Add(TEntity entity);
		TEntity Update(int id, TEntity entity);
		bool Delete(int id);
		int GetCurrentId();
	}
}
