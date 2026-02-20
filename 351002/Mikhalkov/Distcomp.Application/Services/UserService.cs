using AutoMapper;
using Distcomp.Application.DTOs;
using Distcomp.Application.Exceptions;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;

namespace Distcomp.Application.Services
{
    public class UserService : IUserService
    {
        private readonly IRepository<User> _repository;
        private readonly IMapper _mapper;

        public UserService(IRepository<User> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        public UserResponseTo Create(UserRequestTo request)
        {
            ValidateUserRequest(request);

            if (_repository.GetAll().Any(u => u.Login == request.Login))
                throw new RestException(403, 40301, "User with this login already exists");

            var user = _mapper.Map<User>(request);
            var createdUser = _repository.Create(user);

            return _mapper.Map<UserResponseTo>(createdUser);
        }

        public UserResponseTo? GetById(long id)
        {
            var user = _repository.GetById(id);
            if (user == null)
                throw new RestException(404, 40401, $"User with id {id} not found");

            return _mapper.Map<UserResponseTo>(user);
        }

        public IEnumerable<UserResponseTo> GetAll()
        {
            var users = _repository.GetAll();
            return _mapper.Map<IEnumerable<UserResponseTo>>(users);
        }

        public UserResponseTo Update(long id, UserRequestTo request)
        {
            var existingUser = _repository.GetById(id);
            if (existingUser == null)
                throw new RestException(404, 40401, $"Cannot update: User with id {id} not found");

            ValidateUserRequest(request);

            if (existingUser.Login != request.Login && _repository.GetAll().Any(u => u.Login == request.Login))
                throw new RestException(403, 40301, "New login is already taken");

            _mapper.Map(request, existingUser);
            existingUser.Id = id; 

            _repository.Update(existingUser);

            return _mapper.Map<UserResponseTo>(existingUser);
        }

        public bool Delete(long id)
        {
            var existingUser = _repository.GetById(id);
            if (existingUser == null)
                throw new RestException(404, 40401, $"Cannot delete: User with id {id} not found");

            return _repository.Delete(id);
        }

        private void ValidateUserRequest(UserRequestTo request)
        {
            if (request.Login.Length < 2 || request.Login.Length > 64)
                throw new RestException(400, 40001, "Login length must be between 2 and 64");

            if (request.Password.Length < 8 || request.Password.Length > 128)
                throw new RestException(400, 40002, "Password length must be between 8 and 128");

            if (request.FirstName.Length < 2 || request.FirstName.Length > 64)
                throw new RestException(400, 40003, "Firstname length must be between 2 and 64");

            if (request.LastName.Length < 2 || request.LastName.Length > 64)
                throw new RestException(400, 40004, "Lastname length must be between 2 and 64");
        }
    }
}