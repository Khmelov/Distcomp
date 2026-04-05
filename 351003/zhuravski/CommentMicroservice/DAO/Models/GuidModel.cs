using Additions.DAO;

namespace CommentMicroservice.DAO.Models;

public abstract class GuidModel<T> : Model<T, Guid> where T : GuidModel<T> {}