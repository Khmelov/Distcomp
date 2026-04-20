namespace Additions.DAO;

public abstract class LongIdModel<T> : Model<T, long> where T : LongIdModel<T> {}