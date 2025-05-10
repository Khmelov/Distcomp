using AutoMapper;
using Discussion.DTO.Request;
using Discussion.DTO.Response;
using Discussion.Exceptions;
using Discussion.Infrastructure.Validators;
using Discussion.Models;
using Discussion.Repositories.Interfaces;
using Discussion.Services.Interfaces;
using FluentValidation;

namespace Discussion.Services.Implementations;

public class MessageService : IMessageService
{
    private readonly IMessageRepository _messageRepository;
    private readonly IMapper _mapper;
    private readonly MessageRequestDtoValidator _validator;
    
    public MessageService(IMessageRepository messageRepository, 
        IMapper mapper, MessageRequestDtoValidator validator)
    {
        _messageRepository = messageRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<MessageResponseDTO>> GetNoticesAsync()
    {
        var notices = await _messageRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<MessageResponseDTO>>(notices);
    }

    public async Task<MessageResponseDTO> GetNoticeByIdAsync(long id)
    {
        var notice = await _messageRepository.GetByIdAsync(id)
                      ?? throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(id));
        return _mapper.Map<MessageResponseDTO>(notice);
    }

    public async Task<MessageResponseDTO> CreateNoticeAsync(MessageRequestDTO message)
    {
        await _validator.ValidateAndThrowAsync(message);
        var noticeToCreate = _mapper.Map<Message>(message);
        var createdNotice = await _messageRepository.CreateAsync(noticeToCreate);
        return _mapper.Map<MessageResponseDTO>(createdNotice);
    }

    public async Task<MessageResponseDTO> UpdateNoticeAsync(MessageRequestDTO message)
    {
        await _validator.ValidateAndThrowAsync(message);
        var noticeToUpdate = _mapper.Map<Message>(message);
        var updatedNotice = await _messageRepository.UpdateAsync(noticeToUpdate)
                             ?? throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(message.Id));
        return _mapper.Map<MessageResponseDTO>(updatedNotice);
    }

    public async Task DeleteNoticeAsync(long id)
    {
        if (!await _messageRepository.DeleteAsync(id))
        {
            throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(id));
        }
    }
}