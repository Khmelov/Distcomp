using DC_REST.Data;
using DC_REST.Entities;
using Microsoft.EntityFrameworkCore;

namespace DC_REST.Repositories
{
	public class UserDbRepository: IRepository<User>
	{
		private readonly DatabaseContext _dbContext;

		public UserDbRepository(DatabaseContext dbContext)
		{
			_dbContext = dbContext;
		}

		public User GetById(int id)
		{
			return _dbContext.Users.Find(id);
		}

		public List<User> GetAll()
		{
			return _dbContext.Users.ToList();
		}

		public User Add(User entity)
		{
			try
			{
				_dbContext.Users.Add(entity);
				_dbContext.SaveChanges();
				return entity;
			}
			catch (Exception ex)
			{
				throw new DbUpdateException("Violation of uniqueness constraint");
			}
		}

		public User Update(int id, User entity)
		{
			//_dbContext.Entry(entity).State = EntityState.Modified;
			var existingIssue = _dbContext.Users.Find(id);

			if (existingIssue == null)
			{
				throw new ArgumentException("There is no such issue");
			}
			_dbContext.SaveChanges();
			return entity;
		}

		public bool Delete(int id)
		{
			var issue = _dbContext.Users.Find(id);
			if (issue == null)
				return false;

			_dbContext.Users.Remove(issue);
			_dbContext.SaveChanges();
			return true;
		}

		public int GetCurrentId()
		{
			throw new NotImplementedException();
		}
	}
}
