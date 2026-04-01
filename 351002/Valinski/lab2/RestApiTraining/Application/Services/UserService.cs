using Application.Abstractions;
using Application.Dtos;
using Application.Interfaces;
using AutoMapper;

namespace Application.Services;

public class UserService : IUserService
{
    private readonly IUserRepository _userRepository;
    private readonly IMapper _mapper;
    
    public UserService(IUserRepository userRepository, IMapper mapper)
    {
        _userRepository = userRepository;
        _mapper = mapper;
    }

    public async Task<UserGetDto?> CreateUserAsync(UserCreateDto createDto)
    {
        var userFromRepo = await _userRepository.GetUserByLoginAsync(createDto.Login);
        if (userFromRepo != null)
        {
            return null;
        }
        
        var user = await _userRepository.CreateUserAsync(createDto);
        return _mapper.Map<UserGetDto>(user);
    }
    
    public async Task<List<UserGetDto>> GetAllUsers()
    {
        return _mapper.Map<List<UserGetDto>>(await _userRepository.GetAllUsersAsync());
    }

    public async Task<UserGetDto> GetUserById(long id)
    {
        var userFromRepo = await _userRepository.GetUserByIdAsync(id);
        return _mapper.Map<UserGetDto>(userFromRepo);
    }

    public async Task<UserGetDto> UpdateUserAsync(UserUpdateDto updateDto)
    {
        if (await _userRepository.GetUserByIdAsync(updateDto.Id) == null)
        {
            return null;
        }
        
        var user = await _userRepository.UpdateUserAsync(updateDto);
        return _mapper.Map<UserGetDto>(user);
    }

    public async Task<bool> DeleteUserAsync(long id)
    {
        if (await _userRepository.GetUserByIdAsync(id) == null)
        {
            return false;
        }
        
        await _userRepository.DeleteUsersAsync(id);
        return true;
    }
}
