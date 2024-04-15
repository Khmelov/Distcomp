using Publisher.Entity.Db;
using Publisher.Repository.Implementation.Common;
using Publisher.Repository.Interface;
using Publisher.Storage.Common;

namespace Publisher.Repository.Implementation
{
    public class MarkerRepository(DbStorage context) : AbstractCrudRepository<Marker>(context), IMarkerRepository
    {
    }
}
