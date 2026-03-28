using Additions.DAO;

namespace CommentMicroservice.DAO.Models;

public abstract class LongIdModel<T> : Model<T, long> where T : LongIdModel<T> {}