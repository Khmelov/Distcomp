namespace rest_api.InMemory
{
    public class ITopicRepository: IRepository<Topic>
    {
        private Dictionary<long, Topic> _topics;
        public Topic GetById(long id)
        {
            if (_topics.TryGetValue(id, out var topic))
                return topic;
            throw new KeyNotFoundException($"Topic with {id} not found");
        }
        public void Add(Topic topic)
        {
            if (topic == null) 
                throw new ArgumentNullException(nameof(topic));
            if (_topics.ContainsKey(topic.Id))
                throw new InvalidOperationException($"Topic with id {topic.Id} already exists");
        }
        public void Update(Topic topic) {
            if (topic == null) throw new ArgumentNullException(nameof(topic));
            if (!_topics.ContainsKey(topic.Id)) 
                throw new InvalidOperationException($"Topic with {topic.Id} not found");
        }
        public void Delete(long id)
        {
            if (!_topics.Remove(id))
                throw new KeyNotFoundException($"Topic with id {id} not found");
        }
        public IEnumerable<Topic> GetAll() => _topics.Values.ToList();

        public IEnumerable<Topic> Find(Func<Topic, bool> predicate)
        {
            if (predicate == null) throw new ArgumentNullException(nameof(predicate));
            return _topics.Values.Where(predicate).ToList();
        }

    }
}
