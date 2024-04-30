using Microsoft.EntityFrameworkCore;
using RV.Models;

namespace RV.Repositories.SQLRepositories
{
    public class SQLNoteRepository : IRepository<Note>
    {
        private ApplicationContext _dbContext;

        public SQLNoteRepository(ApplicationContext dbContext)
        {
            _dbContext = dbContext;
        }
        public Note Create(Note item)
        {
            _dbContext.Add(item);
            _dbContext.SaveChanges();
            return item;
        }

        public int Delete(int id)
        {
            int res = _dbContext.Notes.Where(n => n.id == id).ExecuteDelete();
            _dbContext.SaveChanges();
            return res;
        }

        public Note Get(int id)
        {
            var res = _dbContext.Notes.Where(n => n.id == id).ToList();
            Note n;
            if (res.Count > 0)
            {
                n = res[0];
                return n;
            }
            else return null;
        }

        public List<Note> GetAll()
        {            
            return _dbContext.Notes.ToList();
        }

        public Note Update(Note item)
        {
            var res = _dbContext.Notes.Where(n => n.id == item.id).ToList();
            Note n;
            if (res.Count > 0)
            {
                n = res[0];
            }
            else return null;
            if (item.tweetId != null)
            {
                n.tweetId = (int)item.tweetId;
            }
            if (item.content != null)
            {
                n.content = item.content;
            }
            _dbContext.Update(n);
            _dbContext.SaveChanges();
            return n;
        }
    }
}
