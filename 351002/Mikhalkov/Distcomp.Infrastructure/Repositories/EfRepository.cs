using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;
using Distcomp.Infrastructure.Data;
using Microsoft.EntityFrameworkCore;
using System.Linq.Dynamic.Core;

namespace Distcomp.Infrastructure.Repositories
{
    public class EfRepository<T> : IRepository<T> where T : class
    {
        private readonly AppDbContext _context;
        private readonly DbSet<T> _dbSet;

        public EfRepository(AppDbContext context)
        {
            _context = context;
            _dbSet = context.Set<T>();
        }

        public T Create(T entity)
        {
            _dbSet.Add(entity);
            _context.SaveChanges();
            return entity;
        }

        public T? GetById(long id)
        {
            if (typeof(T) == typeof(Issue))
            {
                return _dbSet
                    .Include("Markers")
                    .Include("Notes")
                    .FirstOrDefault(e => EF.Property<long>(e, "Id") == id);
            }
            return _dbSet.Find(id);
        }

        public IEnumerable<T> GetAll() => _dbSet.ToList();

        public IEnumerable<T> GetPaged(int page, int pageSize, string sortBy)
        {
            IQueryable<T> query = _dbSet;

            return query
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .ToList();
        }

        public T Update(T entity)
        {
            _context.SaveChanges();
            return entity;
        }

        public bool Delete(long id)
        {
            var entity = GetById(id);
            if (entity == null) return false;
            _dbSet.Remove(entity);
            _context.SaveChanges();
            return true;
        }
    }
}