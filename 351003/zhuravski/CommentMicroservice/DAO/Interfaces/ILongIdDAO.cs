using Additions.DAO;

namespace CommentMicroservice.DAO.Interfaces;

public interface ILongIdDAO<T> : IBasicDAO<T, long> where T : Model<T, long> {}