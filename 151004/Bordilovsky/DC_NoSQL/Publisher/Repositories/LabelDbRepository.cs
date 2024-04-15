using DC_REST.Data;
using DC_REST.Entities;
using Microsoft.EntityFrameworkCore;

namespace DC_REST.Repositories
{
	public class LabelDbRepository:IRepository<Label>
	{
		private readonly DatabaseContext _dbContext;

		public LabelDbRepository(DatabaseContext dbContext)
		{
			_dbContext = dbContext;
		}

		public Label GetById(int id)
		{
			return _dbContext.Labels.Find(id);
		}

		public List<Label> GetAll()
		{
			return _dbContext.Labels.ToList();
		}

		public Label Add(Label entity)
		{
			try
			{
				_dbContext.Labels.Add(entity);
				_dbContext.SaveChanges();
				return entity;
			}
			catch (Exception)
			{
				throw new DbUpdateException("Violation of uniqueness constraint");
			}
		}

		public Label Update(int id, Label entity)
		{
			//_dbContext.Entry(entity).State = EntityState.Modified;
			var existingIssue = _dbContext.Labels.Find(id);

			if (existingIssue == null)
			{
				throw new ArgumentException("There is no such issue");
			}
			_dbContext.SaveChanges();
			return entity;
		}

		public bool Delete(int id)
		{
			var issue = _dbContext.Labels.Find(id);
			if (issue == null)
				return false;

			_dbContext.Labels.Remove(issue);
			_dbContext.SaveChanges();
			return true;
		}

		public int GetCurrentId()
		{
			throw new NotImplementedException();
		}
	}
}
