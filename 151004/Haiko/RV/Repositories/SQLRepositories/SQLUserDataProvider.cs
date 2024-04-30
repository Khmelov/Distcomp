using Microsoft.EntityFrameworkCore;
using RV.Models;
using System.Data;

namespace RV.Repositories.SQLRepositories
{
    public class SQLUserRepository : IRepository<User>
    {
        private ApplicationContext _dbContext;

        public SQLUserRepository(ApplicationContext dbContext)
        {
            _dbContext = dbContext;
        }
        public User Create(User item)
        {
            _dbContext.Add(item);
            _dbContext.SaveChanges();      
            return item;
        }

        public int Delete(int id)
        {
            int res = _dbContext.Users.Where(u => u.id == id).ExecuteDelete();
            _dbContext.SaveChanges();
            return res;
        }

        public User Get(int id)
        {
            var res = _dbContext.Users.Where(u => u.id == id).ToList();
            User u;
            if (res.Count > 0)
            {
                u = res[0];
                return u;
            }
            else return null;
        }

        public List<User> GetAll()
        {
            return _dbContext.Users.ToList();
        }

        public User Update(User item)
        {
            var res = _dbContext.Users.Where(u => u.id == item.id).ToList();
            User u;
            if (res.Count > 0)
            {
                u = res[0];
            }
            else return null;
            if (item.login != null)
            {
                u.login = item.login;
            }
            if (item.lastname != null) 
            { 
                u.lastname = item.lastname;
            }
            if (item.firstname != null)
            {
                u.firstname = item.firstname;
            }
            if (item.password != null)
            {
                u.password = item.password;
            }
            _dbContext.Update(u);
            _dbContext.SaveChanges();
            return u;
        }
    }
}
