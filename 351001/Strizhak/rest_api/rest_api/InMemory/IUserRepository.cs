namespace rest_api.InMemory
{
    public class IUserRepository : IRepository<User>
    {
        private Dictionary<long, User> _users;
        public User GetById(long id)
        {
            if (_users.TryGetValue(id, out var user))
                return user;
            throw new KeyNotFoundException(($"User with id {id} not found"));
        }
        public void Add(User user)
        {
            if (user == null) 
                throw new ArgumentNullException(nameof(user));
            if (_users.ContainsKey(user.Id)) 
                throw new InvalidOperationException($"User with id {user.Id} already exists");
            _users.Add(user.Id, user);
        }
        public void Update(User user)
        {
            if (user == null) throw new ArgumentNullException(nameof(user));
            if (!_users.ContainsKey(user.Id)) throw new InvalidOperationException($"User with id {user.Id} not found");
            _users[user.Id] = user;
        }
        public void Delete(long id)
        {
            if (!_users.Remove(id))
                throw new KeyNotFoundException($"User with id {id} not found");

        }
        public IEnumerable<User> GetAll() => _users.Values.ToList();

        public IEnumerable<User> Find(Func<User, bool> predicate)
        {
            if (predicate == null) throw new ArgumentNullException(nameof(predicate));
            return _users.Values.Where(predicate).ToList();
        }
    }
}
