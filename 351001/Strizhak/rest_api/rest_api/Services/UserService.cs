using rest_api.Entities;
using rest_api.InMemory;
using rest_api.Dtos;
using BCrypt.Net;

namespace rest_api.Services
{
    public class UserService : BaseService<User, UserRequestTo, UserResponseTo>
    {
        public UserService(IRepository<User> repository) : base(repository)
        {
        }

        public override UserResponseTo Create(UserRequestTo request)
        {
            var existing = _repository.Find(u => u.Login == request.Login).FirstOrDefault();
            if (existing != null)
                throw new InvalidOperationException("Login already exists");

            var user = MapToEntity(request);
            user.Password = BCrypt.Net.BCrypt.HashPassword(request.Password); 

            _repository.Add(user);
            return MapToResponse(user);
        }

        public override UserResponseTo Update(UserRequestTo request)
        {
            var id = request.Id;
            var user = _repository.GetById(id);
            if (user == null)
                throw new KeyNotFoundException($"User with id {id} not found");

            // Проверка уникальности логина (если он меняется)
            if (user.Login != request.Login)
            {
                var existing = _repository.Find(u => u.Login == request.Login).FirstOrDefault();
                if (existing != null)
                    throw new InvalidOperationException("Login already exists");
            }

            user.Login = request.Login;
            user.Firstname = request.Firstname;
            user.Lastname = request.Lastname;
            if (!string.IsNullOrWhiteSpace(request.Password))
                user.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);

            _repository.Update(user);
            return MapToResponse(user);
        }

        protected override UserResponseTo MapToResponse(User entity)
        {
            return new UserResponseTo
            {
                Id = entity.Id,
                Login = entity.Login,
                Firstname = entity.Firstname,
                Lastname = entity.Lastname
            };
        }

        protected override User MapToEntity(UserRequestTo request)
        {
            return new User
            {
                Login = request.Login,
                Firstname = request.Firstname,
                Lastname = request.Lastname
                // Id будет сгенерирован репозиторием, пароль установим отдельно
            };
        }
    }
}