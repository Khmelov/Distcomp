using distcomp.Models;
using System.Collections.Generic;
using System.Linq;

namespace distcomp.Repositories
{
    public class CreatorRepository : ICrudRepository<Creator, long>
    {
        private readonly Dictionary<long, Creator> _creators = new();
        private long _idCounter = 0;

        public Creator Save(Creator creator)
        {
            creator.Id = ++_idCounter;
            _creators[creator.Id] = creator; 
            return creator;
        }

        public Creator FindById(long id)
        {
            return _creators.TryGetValue(id, out var creator) ? creator : null;
        }

        public List<Creator> FindAll()
        {
            return _creators.Values.ToList();
        }

        public Creator Update(Creator creator)
        {
            if (_creators.ContainsKey(creator.Id))
            {
                _creators[creator.Id] = creator; 
                return creator;
            }
            return null;
        }

        public bool DeleteById(long id)
        {
            return _creators.Remove(id); 
        }

    }
}
