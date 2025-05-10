using AutoMapper;
using FluentValidation;
using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;
using Publisher.Exceptions;
using Publisher.Infrastructure.Validators;
using Publisher.Models;
using Publisher.Repositories;
using Publisher.Repositories.Implementations;
using Publisher.Repositories.Interfaces;
using Publisher.Services.Interfaces;
using ValidationException = System.ComponentModel.DataAnnotations.ValidationException;

namespace Publisher.Services.Implementations;

public class AuthorService : IAuthorService
{
    private readonly IAuthorRepository _iAuthorRepository;
    private readonly IMapper _mapper;
    private readonly AuthorRequestDTOValidator _validator;
    
    public AuthorService(IAuthorRepository iAuthorRepository, 
        IMapper mapper, AuthorRequestDTOValidator validator)
    {
        _iAuthorRepository = iAuthorRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<AuthorResponseDTO>> GetUsersAsync()
    {
        var users = await _iAuthorRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<AuthorResponseDTO>>(users);
    }

    public async Task<AuthorResponseDTO> GetUserByIdAsync(long id)
    {
        var user = await _iAuthorRepository.GetByIdAsync(id)
                      ?? throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(id));
        return _mapper.Map<AuthorResponseDTO>(user);
    }

    public async Task<AuthorResponseDTO> CreateUserAsync(AuthorRequestDTO author)
    {
        await _validator.ValidateAndThrowAsync(author);
        var userToCreate = _mapper.Map<Author>(author);
        var createdUser = await _iAuthorRepository.CreateAsync(userToCreate);
        return _mapper.Map<AuthorResponseDTO>(createdUser);
    }

    public async Task<AuthorResponseDTO> UpdateUserAsync(AuthorRequestDTO author)
    {
        await _validator.ValidateAndThrowAsync(author);
        var userToUpdate = _mapper.Map<Author>(author);
        var updatedUser = await _iAuthorRepository.UpdateAsync(userToUpdate)
                             ?? throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(author.Id));
        return _mapper.Map<AuthorResponseDTO>(updatedUser);
    }

    public async Task DeleteUserAsync(long id)
    {
        if (!await _iAuthorRepository.DeleteAsync(id))
        {
            throw new NotFoundException(ErrorCodes.UserNotFound, ErrorMessages.UserNotFoundMessage(id));
        }
    }
}