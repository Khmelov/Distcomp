using Additions.DAO;

namespace ArticleHouse.DAO.Models;

public abstract class LongIdModel<T> : Model<T, long> where T : LongIdModel<T> {}