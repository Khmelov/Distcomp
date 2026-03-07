using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

public class DbCommentDAO : DbDAO<CommentModel, DbSet<CommentModel>>, ICommentDAO
{
    public DbCommentDAO(ApplicationContext db) : base(db, (x) => x.Comments) {}
}