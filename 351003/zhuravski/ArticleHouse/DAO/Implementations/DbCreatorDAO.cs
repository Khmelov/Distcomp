using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

public class DbCreatorDAO : DbDAO<CreatorModel>, ICreatorDAO
{
    public DbCreatorDAO(ApplicationContext db) : base(db, (x) => x.Creators) {}
}