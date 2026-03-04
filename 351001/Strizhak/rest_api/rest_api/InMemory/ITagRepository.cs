using rest_api.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace rest_api.InMemory
{
    public class ITagRepository : IRepository<Tag>
    {
        private readonly Dictionary<long, Tag> _tags;

        public ITagRepository()
        {
            _tags = new Dictionary<long, Tag>();
        }

        public Tag GetById(long id)
        {
            if (_tags.TryGetValue(id, out var tag))
                return tag;
            throw new KeyNotFoundException($"Tag with id {id} not found");
        }

        public void Add(Tag tag)
        {
            if (tag == null)
                throw new ArgumentNullException(nameof(tag));
            if (_tags.ContainsKey(tag.Id))
                throw new InvalidOperationException($"Tag with id {tag.Id} already exists");
            _tags.Add(tag.Id, tag);
        }

        public void Update(Tag tag)
        {
            if (tag == null)
                throw new ArgumentNullException(nameof(tag));
            if (!_tags.ContainsKey(tag.Id))
                throw new InvalidOperationException($"Tag with id {tag.Id} not found");
            _tags[tag.Id] = tag;
        }

        public void Delete(long id)
        {
            if (!_tags.Remove(id))
                throw new KeyNotFoundException($"Tag with id {id} not found");
        }

        public IEnumerable<Tag> GetAll() => _tags.Values.ToList();

        public IEnumerable<Tag> Find(Func<Tag, bool> predicate)
        {
            if (predicate == null)
                throw new ArgumentNullException(nameof(predicate));
            return _tags.Values.Where(predicate).ToList();
        }
    }
}