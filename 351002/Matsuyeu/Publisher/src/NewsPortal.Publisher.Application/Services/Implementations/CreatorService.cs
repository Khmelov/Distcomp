using Publisher.src.NewsPortal.Publisher.Application.Dtos.Auth;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Repositories.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Application.Services.Implementations;

public class CreatorService : ICreatorService
{
    private readonly IRepository<Creator> _creatorRepository;
    private readonly IJwtService _jwtService;

    public CreatorService(IRepository<Creator> creatorRepository, IJwtService jwtService)
    {
        _creatorRepository = creatorRepository;
        _jwtService = jwtService;
    }

    public async Task<IEnumerable<CreatorResponseTo>> GetAllCreatorsAsync()
    {
        var creators = await _creatorRepository.GetAllAsync();
        return creators.Select(c => MapToResponse(c));
    }

    public async Task<CreatorResponseTo?> GetCreatorByIdAsync(long id)
    {
        if (id <= 0)
            throw new BadRequestException("ID must be greater than 0");

        var creator = await _creatorRepository.GetByIdAsync(id);
        if (creator == null)
            throw new NotFoundException($"Creator with ID {id} not found");

        return MapToResponse(creator);
    }

    public async Task<CreatorResponseTo> GetCreatorByLoginAsync(string login)
    {
        if (string.IsNullOrWhiteSpace(login))
            throw new BadRequestException("Login cannot be empty");

        var creator = await _creatorRepository.FindSingleAsync(c => c.Login == login);
        if (creator == null)
            throw new NotFoundException($"Creator with login '{login}' not found");

        return MapToResponse(creator);
    }

    public async Task<CreatorResponseTo> CreateCreatorAsync(CreatorRequestTo creatorRequest)
    {
        // Проверка уникальности логина
        var existingCreator = await _creatorRepository.FindSingleAsync(c => c.Login == creatorRequest.Login);
        if (existingCreator != null)
            throw new ConflictException($"Login '{creatorRequest.Login}' is already taken");

        var creator = new Creator
        {
            Login = creatorRequest.Login,
            Password = BCrypt.Net.BCrypt.HashPassword(creatorRequest.Password),
            FirstName = creatorRequest.FirstName,
            LastName = creatorRequest.LastName,
            Role = "CUSTOMER"
        };

        var addedCreator = await _creatorRepository.AddAsync(creator);
        return MapToResponse(addedCreator);
    }

    public async Task<CreatorResponseTo> RegisterAsync(RegisterRequestDto registerRequest)
    {
        //Проверка уникальности логина
        var existingCreator = await _creatorRepository.FindSingleAsync(c => c.Login == registerRequest.Login);
        if (existingCreator != null)
            throw new ConflictException($"Login '{registerRequest.Login}' is already taken");

        var role = registerRequest.Role?.ToUpper();
        if (role != "ADMIN" && role != "CUSTOMER")
            role = "CUSTOMER";

        var creator = new Creator
        {
            Login = registerRequest.Login,
            Password = BCrypt.Net.BCrypt.HashPassword(registerRequest.Password),
            FirstName = registerRequest.FirstName,
            LastName = registerRequest.LastName,
            Role = role
        };

        var addedCreator = await _creatorRepository.AddAsync(creator);
        return MapToResponse(addedCreator);
    }

    public async Task<LoginResponseDto> LoginAsync(LoginRequestDto loginRequest)
    {
        var creator = await _creatorRepository.FindSingleAsync(c => c.Login == loginRequest.Login);
        if (creator == null)
            throw new UnauthorizedException("Invalid login or password");

        if (!BCrypt.Net.BCrypt.Verify(loginRequest.Password, creator.Password))
            throw new UnauthorizedException("Invalid login or password");

        var token = _jwtService.GenerateToken(creator.Login, creator.Role);

        return new LoginResponseDto
        {
            AccessToken = token,
            TokenType = "Bearer"
        };
    }

    public async Task<bool> UpdateCreatorAsync(CreatorRequestTo creatorRequest)
    {
        if (creatorRequest.Id <= 0)
            throw new BadRequestException("ID must be greater than 0");

        var existingCreator = await _creatorRepository.GetByIdAsync(creatorRequest.Id);
        if (existingCreator == null)
            throw new NotFoundException($"Creator with ID {creatorRequest.Id} not found");

        // Проверка уникальности логина (исключая текущего)
        var duplicateCreator = await _creatorRepository.FindSingleAsync(c =>
            c.Login == creatorRequest.Login && c.Id != creatorRequest.Id);
        if (duplicateCreator != null)
            throw new ConflictException($"Login '{creatorRequest.Login}' is already taken");

        existingCreator.Login = creatorRequest.Login;
        if (!string.IsNullOrWhiteSpace(creatorRequest.Password))
        {
            existingCreator.Password = BCrypt.Net.BCrypt.HashPassword(creatorRequest.Password);
        }
        existingCreator.FirstName = creatorRequest.FirstName;
        existingCreator.LastName = creatorRequest.LastName;

        await _creatorRepository.UpdateAsync(existingCreator);
        return true;
    }

    public async Task<bool> DeleteCreatorAsync(long id)
    {
        if (id <= 0)
            throw new BadRequestException("ID must be greater than 0");

        //Проверяем существование перед удалением
        var existingCreator = await _creatorRepository.GetByIdAsync(id);
        if (existingCreator == null)
            throw new NotFoundException($"Creator with ID {id} not found");

        await _creatorRepository.DeleteAsync(id);
        return true;
    }

    public async Task<PagedResult<CreatorResponseTo>> GetPagedCreatorsAsync(QueryParameters parameters)
    {
        var pagedResult = await _creatorRepository.GetPagedAsync(parameters);

        return new PagedResult<CreatorResponseTo>
        {
            Items = pagedResult.Items.Select(MapToResponse),
            TotalCount = pagedResult.TotalCount,
            PageNumber = pagedResult.PageNumber,
            PageSize = pagedResult.PageSize
        };
    }

    private CreatorResponseTo MapToResponse(Creator creator)
    {
        return new CreatorResponseTo
        {
            Id = creator.Id,
            Login = creator.Login,
            FirstName = creator.FirstName,
            LastName = creator.LastName,
            Role = creator.Role
        };
    }
}