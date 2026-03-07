using Additions.DAO;
using CommentMicroservice.DAO.Models;

namespace CommentMicroservice.DAO.Interfaces;

public interface ICommentDAO : IBasicDAO<CommentModel, long> {}