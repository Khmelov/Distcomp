using distcomp.Models;
using System.Collections.Generic;
using System.Linq;

namespace distcomp.Repositories
{
    public class TagRepository : ICrudRepository<Tag, long>
    {
        private readonly Dictionary<long, Tag> _tags = new();
        private long _idCounter = 0;

        public Tag Save(Tag tag)
        {
            tag.Id = ++_idCounter; //generate id
            _tags[tag.Id] = tag; //in-memory save
            return tag;
        }

        public Tag FindById(long id)
        {
            return _tags.TryGetValue(id, out var tag) ? tag : null;
        }

        public List<Tag> FindAll()
        {
            return _tags.Values.ToList();
        }

        public Tag Update(Tag tag)
        {
            if (_tags.ContainsKey(tag.Id))
            {
                _tags[tag.Id] = tag; 
                return tag;
            }
            return null; 
        }


        public bool DeleteById(long id)
        {
            return _tags.Remove(id); 
        }
    }
}
