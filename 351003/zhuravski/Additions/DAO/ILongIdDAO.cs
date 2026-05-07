namespace Additions.DAO;

public interface ILongIdDAO<T> : IBasicDAO<T, long> where T : Model<T, long> {}