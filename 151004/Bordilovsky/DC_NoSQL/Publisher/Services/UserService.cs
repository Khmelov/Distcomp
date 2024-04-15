using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;
using DC_REST.Repositories;
using DC_REST.Services.Interfaces;
using DC_REST.Validators;

namespace DC_REST.Services
{
    public class UserService : IUserService
	{
		private readonly IRepository<User> _userRepository;
		private readonly IMapper _mapper;
		private readonly IValidator<UserRequestTo> _userValidator;

		public UserService(IRepository<User> userRepository, IMapper mapper, IValidator<UserRequestTo> userValidator)
		{
			_userRepository = userRepository;
			_mapper = mapper;
			_userValidator = userValidator;
		}

		public UserResponseTo CreateUser(UserRequestTo userRequestDto)
		{
			if (!_userValidator.Validate(userRequestDto))
			{
				throw new ArgumentException("Invalid user data");
			}
			var user = _mapper.Map<User>(userRequestDto);
			//var currentId = _userRepository.GetCurrentId();
			//user.Id = currentId;
			var createdUser = _userRepository.Add(user);
			var responseDto = _mapper.Map<UserResponseTo>(createdUser);

			return responseDto;
		}

		public UserResponseTo GetUserById(int id)
		{
			var user = _userRepository.GetById(id);
			var userDto = _mapper.Map<UserResponseTo>(user);

			return userDto;
		}

		public List<UserResponseTo> GetAllUsers()
		{
			var users = _userRepository.GetAll();
			var usersDTO = _mapper.Map<List<UserResponseTo>>(users);

			return usersDTO;
		}

		public UserResponseTo UpdateUser(int id, UserRequestTo userRequestDTO)
		{

			if (!_userValidator.Validate(userRequestDTO)) 
			{
				throw new ArgumentException("Invalid user data");
			}

			var existingUser = _userRepository.GetById(id);
			if (existingUser == null)
			{
				return null;
			}

			_mapper.Map(userRequestDTO, existingUser);
			var updatedUser = _userRepository.Update(id, existingUser);
			var responseDto = _mapper.Map<UserResponseTo>(updatedUser);

			return responseDto;
		}

		public bool DeleteUser(int id)
		{
			var userToDelete = _userRepository.GetById(id);
			if (userToDelete == null)
			{
				return false;
			}

			_userRepository.Delete(id);
			return true;
		}
	}

}
