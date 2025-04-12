namespace DistComp.Services {
    public interface ICommonService<K, T, I, O> {

        IEnumerable<O> GetAll();

        O? Get(K id);

        O Create(I data);

        O Update(T data);

        void Delete(K id);
    }
}
