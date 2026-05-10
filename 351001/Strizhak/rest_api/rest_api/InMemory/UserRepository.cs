namespace rest_api.InMemory
{
    public class UserRepository : IRepository<User>
    {
        private Dictionary<long, User> _users;
        private long _nextId = 1;
        public UserRepository()
        {
            _users = new Dictionary<long, User>();
            _users = new Dictionary<long, User>();
            var firstUser = new User
            {
                Id = 1,
                Login = "veranikastryzhak@gmail.com",
                Password = BCrypt.Net.BCrypt.HashPassword("password123"),
                Firstname = "Veranika",
                Lastname = "Stryzhak"
            };
            _users.Add(firstUser.Id, firstUser);
            _nextId = 2;
        }

        public User GetById(long id)
        {
            if (_users.TryGetValue(id, out var user))
                return user;
            return null;
        }
        public void Add(User user)
        {
            if (user == null) 
                throw new ArgumentNullException(nameof(user));
            
            user.Id = _nextId++;
            _users.Add(user.Id, user);
        }
        public void Update(User user)
        {
            if (user == null) throw new ArgumentNullException(nameof(user));
            if (!_users.ContainsKey(user.Id)) 
                throw new InvalidOperationException($"User with id {user.Id} not found");
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
