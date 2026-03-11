using rest_api.Entities;

namespace rest_api.InMemory
{
    public class TopicRepository : IRepository<Topic>
    {
        // Инициализируем словарь сразу
        private readonly Dictionary<long, Topic> _topics = new();
        private long _nextId = 1;

        public Topic GetById(long id)
        {
            if (_topics.TryGetValue(id, out var topic))
                return topic;

            return null; // Или throw new KeyNotFoundException, если так требует интерфейс
        }

        public void Add(Topic topic)
        {
            if (topic == null)
                throw new ArgumentNullException(nameof(topic));

            // ПРИСВАИВАЕМ НОВЫЙ ID
            topic.Id = _nextId++;

            // СОХРАНЯЕМ В СЛОВАРЬ
            _topics.Add(topic.Id, topic);
        }

        public void Update(Topic topic)
        {
            if (topic == null) throw new ArgumentNullException(nameof(topic));

            if (!_topics.ContainsKey(topic.Id))
                throw new InvalidOperationException($"Topic with id {topic.Id} not found");
            _topics[topic.Id] = topic;
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