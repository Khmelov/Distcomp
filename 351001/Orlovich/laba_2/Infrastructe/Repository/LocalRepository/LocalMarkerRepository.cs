using Domain.Models;

namespace Infrastructe.Repository.LocalRepository;

public class LocalMarkerRepository : BaseLocalRepository<Marker>
{
    public override async Task<Marker?> UpdateAsync(long id, Marker tag)
    {
        await Task.CompletedTask;
        var a = _storage.Find(a => a.id == id);
        
        if(a == null)
            return null;
        
        a.name = tag.name;
        
        return Copy(a);
    }

    public override Marker Copy(Marker src)
    {
        return new Marker()
        {
            id = src.id,
            name = src.name,
        };
    }
}