using DC_REST.Data;
using DC_REST.Entities;
using Microsoft.EntityFrameworkCore;
using System;

namespace DC_REST.Repositories
{
	public class IssueDbRepository:IRepository<Issue>
	{
		private readonly DatabaseContext _dbContext;

		public IssueDbRepository(DatabaseContext dbContext)
		{
			_dbContext = dbContext;
		}

		public Issue GetById(int id)
		{
			return _dbContext.Issues.Find(id);
		}

		public List<Issue> GetAll()
		{
			return _dbContext.Issues.ToList();
		}

		public Issue Add(Issue entity)
		{
			try
			{
				_dbContext.Issues.Add(entity);
				_dbContext.SaveChanges();
				return entity;
			}
			catch (Exception)
			{
				throw new DbUpdateException("Violation of uniqueness constraint");
			}
		}

		public Issue Update(int id, Issue entity)
		{
			//_dbContext.Entry(entity).State = EntityState.Modified;
			var existingIssue = _dbContext.Issues.Find(id); 

			if (existingIssue == null)
			{
				throw new ArgumentException("There is no such issue");
			}
			_dbContext.SaveChanges();
			return entity;
		}

		public bool Delete(int id)
		{
			var issue = _dbContext.Issues.Find(id);
			if (issue == null)
				return false;

			_dbContext.Issues.Remove(issue);
			_dbContext.SaveChanges();
			return true;
		}

		public int GetCurrentId()
		{

			throw new NotImplementedException();
		}
	}
}
