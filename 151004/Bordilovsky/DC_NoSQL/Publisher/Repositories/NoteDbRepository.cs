using DC_REST.Data;
using DC_REST.Entities;
using Microsoft.EntityFrameworkCore;

namespace DC_REST.Repositories
{
	public class NoteDbRepository : IRepository<Note>
	{
		private readonly DatabaseContext _dbContext;

		public NoteDbRepository(DatabaseContext dbContext)
		{
			_dbContext = dbContext;
		}

		public Note GetById(int id)
		{
			return _dbContext.Notes.Find(id);
		}

		public List<Note> GetAll()
		{
			return _dbContext.Notes.ToList();
		}

		public Note Add(Note entity)
		{
			try
			{
				_dbContext.Notes.Add(entity);
				_dbContext.SaveChanges();
				return entity;
			}
			catch (Exception)
			{
				throw new DbUpdateException("Violation of uniqueness constraint");
			}
		}

		public Note Update(int id, Note entity)
		{
			//_dbContext.Entry(entity).State = EntityState.Modified;
			var existingIssue = _dbContext.Notes.Find(id);

			if (existingIssue == null)
			{
				throw new ArgumentException("There is no such issue");
			}
			_dbContext.SaveChanges();
			return entity;
		}

		public bool Delete(int id)
		{
			var issue = _dbContext.Notes.Find(id);
			if (issue == null)
				return false;

			_dbContext.Notes.Remove(issue);
			_dbContext.SaveChanges();
			return true;
		}

		public int GetCurrentId()
		{
			throw new NotImplementedException();
		}
	}
}

