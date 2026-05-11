namespace lab_1.Services;

public interface IAsyncService<T,V>
{
        
        public Task<V> CreateAsync(T dto);
        
        public Task<V> ReadAsync(long id);
        
        public Task<bool> DeleteAsync(long id);
        
        public Task<IEnumerable<V>> GetAllAsync();
        
        public Task<V> UpdateAsync(T dto);
    
}