using AutoMapper;
using BCrypt.Net;
using publisherApi.Models.DTOs;
using publisherApi.Services.Interfaces;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;

namespace publisherApi.Services
{
    public class AuthService : IAuthService
    {
        private readonly IRepository<Writer> _writerRepository;
        private readonly IJwtService _jwtService;
        private readonly IMapper _mapper;
        private readonly ILogger<AuthService> _logger;

        public AuthService(
            IRepository<Writer> writerRepository,
            IJwtService jwtService,
            IMapper mapper,
            ILogger<AuthService> logger)
        {
            _writerRepository = writerRepository;
            _jwtService = jwtService;
            _mapper = mapper;
            _logger = logger;
        }

        public async Task<Writer?> GetByLoginAsync(string login)
        {
            var writers = await _writerRepository.GetAllAsync();
            return writers.FirstOrDefault(w => w.Login == login);
        }

        public async Task<LoginResponseTo> LoginAsync(LoginRequestTo request)
        {
            _logger.LogInformation($"Login attempt for user: {request.Login}");

            var writer = await GetByLoginAsync(request.Login);
            if (writer == null)
            {
                _logger.LogWarning($"Login failed: User {request.Login} not found");
                throw new UnauthorizedException("Invalid login or password");
            }

            if (!BCrypt.Net.BCrypt.Verify(request.Password, writer.Password))
            {
                _logger.LogWarning($"Login failed: Invalid password for user {request.Login}");
                throw new UnauthorizedException("Invalid login or password");
            }

            var token = _jwtService.GenerateToken(writer);
            _logger.LogInformation($"User {request.Login} logged in successfully");

            return new LoginResponseTo(token, "Bearer");
        }

        public async Task<WriterResponseTo> RegisterAsync(RegisterRequestTo request)
        {
            _logger.LogInformation($"Registration attempt for user: {request.Login}");

            // Validate input
            if (string.IsNullOrWhiteSpace(request.Login) || request.Login.Length < 2 || request.Login.Length > 64)
                throw new ValidationException("Login must be between 2 and 64 characters");

            if (string.IsNullOrWhiteSpace(request.Password) || request.Password.Length < 8 || request.Password.Length > 128)
                throw new ValidationException("Password must be between 8 and 128 characters");

            if (string.IsNullOrWhiteSpace(request.FirstName) || request.FirstName.Length < 2 || request.FirstName.Length > 64)
                throw new ValidationException("FirstName must be between 2 and 64 characters");

            if (string.IsNullOrWhiteSpace(request.LastName) || request.LastName.Length < 2 || request.LastName.Length > 64)
                throw new ValidationException("LastName must be between 2 and 64 characters");

            // Check if user already exists
            var existingWriter = await GetByLoginAsync(request.Login);
            if (existingWriter != null)
                throw new ValidationException("User with this login already exists");

            // Create new writer
            var hashedPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);
            var writer = new Writer
            {
                Login = request.Login,
                Password = hashedPassword,
                Firstname = request.FirstName,
                Lastname = request.LastName,
                Role = request.Role ?? "CUSTOMER" // Default role
            };

            var created = await _writerRepository.AddAsync(writer);
            _logger.LogInformation($"User {request.Login} registered successfully with role {created.Role}");

            return _mapper.Map<WriterResponseTo>(created);
        }
    }
}
