using AutoMapper;
using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.DTO.ResponseDTO;
using Distributed_Computing_Lab_2.Exceptions;
using Distributed_Computing_Lab_2.Infrastructure.Validators;
using Distributed_Computing_Lab_2.Models;
using Distributed_Computing_Lab_2.Repositories.Interfaces;
using Distributed_Computing_Lab_2.Services.Interfaces;
using FluentValidation;

namespace Distributed_Computing_Lab_2.Services.Implementations;

public class AuthorService : IAuthorService
{
    private readonly IAuthorRepository _authorRepository;
    private readonly IMapper _mapper;
    private readonly AuthorRequestDTOValidator _validator;
    
    public AuthorService(IAuthorRepository authorRepository, 
        IMapper mapper, AuthorRequestDTOValidator validator)
    {
        _authorRepository = authorRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<AuthorResponseDTO>> GetUsersAsync()
    {
        var users = await _authorRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<AuthorResponseDTO>>(users);
    }

    public async Task<AuthorResponseDTO> GetUserByIdAsync(long id)
    {
        var user = await _authorRepository.GetByIdAsync(id)
                   ?? throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(id));
        return _mapper.Map<AuthorResponseDTO>(user);
    }

    public async Task<AuthorResponseDTO> CreateUserAsync(AuthorRequestDTO author)
    {
        await _validator.ValidateAndThrowAsync(author);
        var userToCreate = _mapper.Map<Author>(author);
        var createdUser = await _authorRepository.CreateAsync(userToCreate);
        return _mapper.Map<AuthorResponseDTO>(createdUser);
    }

    public async Task<AuthorResponseDTO> UpdateUserAsync(AuthorRequestDTO author)
    {
        await _validator.ValidateAndThrowAsync(author);
        var userToUpdate = _mapper.Map<Author>(author);
        var updatedUser = await _authorRepository.UpdateAsync(userToUpdate)
                          ?? throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(author.Id));
        return _mapper.Map<AuthorResponseDTO>(updatedUser);
    }

    public async Task DeleteUserAsync(long id)
    {
        if (!await _authorRepository.DeleteAsync(id))
        {
            throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(id));
        }
    }
}