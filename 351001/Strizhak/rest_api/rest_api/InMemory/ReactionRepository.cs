using rest_api.Entities;
using System;
using System.Collections.Generic;
using System.Linq;

namespace rest_api.InMemory
{
    public class ReactionRepository : IRepository<Reaction>
    {
        private readonly Dictionary<long, Reaction> _reactions;
        // 1. Добавляем счетчик для генерации ID
        private long _nextId = 1;

        public ReactionRepository()
        {
            _reactions = new Dictionary<long, Reaction>();
        }

        public Reaction GetById(long id)
        {
            if (_reactions.TryGetValue(id, out var reaction))
                return reaction;

            // Возвращаем null, чтобы контроллер мог выдать 404 через проверку на null
            return null;
        }

        public void Add(Reaction reaction)
        {
            if (reaction == null)
                throw new ArgumentNullException(nameof(reaction));

            // 2. Назначаем новый ID и увеличиваем счетчик
            reaction.Id = _nextId++;

            // Теперь проверка ContainsKey не нужна, так как ID всегда уникален
            _reactions.Add(reaction.Id, reaction);
        }

        public void Update(Reaction reaction)
        {
            if (reaction == null)
                throw new ArgumentNullException(nameof(reaction));

            if (!_reactions.ContainsKey(reaction.Id))
                throw new InvalidOperationException($"Reaction with id {reaction.Id} not found");

            _reactions[reaction.Id] = reaction;
        }

        public void Delete(long id)
        {
            if (!_reactions.Remove(id))
                throw new KeyNotFoundException($"Reaction with id {id} not found");
        }

        public IEnumerable<Reaction> GetAll() => _reactions.Values.ToList();

        public IEnumerable<Reaction> Find(Func<Reaction, bool> predicate)
        {
            if (predicate == null)
                throw new ArgumentNullException(nameof(predicate));
            return _reactions.Values.Where(predicate).ToList();
        }
    }
}