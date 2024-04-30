using Microsoft.EntityFrameworkCore;
using RV.Models;

namespace RV.Repositories.SQLRepositories
{
    public class SQLStickerRepository : IRepository<Sticker>
    {
        private ApplicationContext _dbContext;

        public SQLStickerRepository(ApplicationContext dbContext)
        {
            _dbContext = dbContext;
        }
        public Sticker Create(Sticker item)
        {
            _dbContext.Add(item);
            _dbContext.SaveChanges();
            return item;
        }

        public int Delete(int id)
        {
            int res = _dbContext.Stickers.Where(s => s.id == id).ExecuteDelete();
            _dbContext.SaveChanges();
            return res;
        }

        public Sticker Get(int id)
        {
            var res = _dbContext.Stickers.Where(s => s.id == id).ToList();
            Sticker s;
            if (res.Count > 0)
            {
                s = res[0];
                return s;
            }
            else return null;
        }

        public List<Sticker> GetAll()
        {            
            return _dbContext.Stickers.ToList();
        }

        public Sticker Update(Sticker item)
        {
            var res = _dbContext.Stickers.Where(s => s.id == item.id).ToList();
            Sticker s;
            if (res.Count > 0)
            {
                s = res[0];
            }
            else return null;
            if (item.name != null)
            {
                s.name = item.name;
            }
            _dbContext.Update(s);
            _dbContext.SaveChanges();
            return s;
        }
    }
}
