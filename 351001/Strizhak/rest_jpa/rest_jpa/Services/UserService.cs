using AutoMapper;
using rest_api;
using rest_api.Dtos;
using rest_api.Services;
using rest_api.Repositories;
using rest_api.Mapper;

public class UserService : BaseService<User, UserRequestTo, UserResponseTo>
{
    public UserService(IRepository<User> repository, IMapper mapper)
        : base(repository, mapper)
    {
    }

    public override async Task<UserResponseTo> CreateAsync(UserRequestTo request)
    {
        // Проверка уникальности логина
        var existing = (await _repository.FindAsync(u => u.Login == request.Login)).FirstOrDefault();
        if (existing != null)
            throw new InvalidOperationException("Login already exists");

        var user = _mapper.Map<User>(request);
        user.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);

        await _repository.AddAsync(user);
        await _repository.SaveChangesAsync();

        return _mapper.Map<UserResponseTo>(user);
    }

    public override async Task<UserResponseTo> UpdateAsync(long id, UserRequestTo request)
    {
        var user = await _repository.GetByIdAsync(id);
        if (user == null)
            throw new KeyNotFoundException($"User with id {id} not found");

        if (user.Login != request.Login)
        {
            var existing = (await _repository.FindAsync(u => u.Login == request.Login)).FirstOrDefault();
            if (existing != null)
                throw new InvalidOperationException("Login already exists");
        }

        _mapper.Map(request, user); 
        if (!string.IsNullOrWhiteSpace(request.Password))
            user.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);

        _repository.Update(user);
        await _repository.SaveChangesAsync();

        return _mapper.Map<UserResponseTo>(user);
    }
}