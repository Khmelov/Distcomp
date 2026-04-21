namespace Additions.DAO;

public abstract class GuidModel<T> : Model<T, Guid> where T : GuidModel<T> {}