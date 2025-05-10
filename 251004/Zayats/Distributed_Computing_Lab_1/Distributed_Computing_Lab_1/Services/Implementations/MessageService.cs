using AutoMapper;
using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;
using Distributed_Computing_Lab_1.Exceptions;
using Distributed_Computing_Lab_1.Infrastructure.Validators;
using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;
using Distributed_Computing_Lab_1.Services.Interfaces;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Services.Implementations;

public class MessageService : IMessageService
{
    private readonly IMesssageRepository _messsageRepository;
    private readonly IMapper _mapper;
    private readonly MessageRequestDTOValidator _validator;
    
    public MessageService(IMesssageRepository messsageRepository, 
        IMapper mapper, MessageRequestDTOValidator validator)
    {
        _messsageRepository = messsageRepository;
        _mapper = mapper;
        _validator = validator;
    }
    
    public async Task<IEnumerable<MessageResponseDTO>> GetNoticesAsync()
    {
        var notices = await _messsageRepository.GetAllAsync();
        return _mapper.Map<IEnumerable<MessageResponseDTO>>(notices);
    }

    public async Task<MessageResponseDTO> GetNoticeByIdAsync(long id)
    {
        var notice = await _messsageRepository.GetByIdAsync(id)
                      ?? throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(id));
        return _mapper.Map<MessageResponseDTO>(notice);
    }

    public async Task<MessageResponseDTO> CreateNoticeAsync(MessageRequestDTO message)
    {
        await _validator.ValidateAndThrowAsync(message);
        var noticeToCreate = _mapper.Map<Message>(message);
        var createdNotice = await _messsageRepository.CreateAsync(noticeToCreate);
        return _mapper.Map<MessageResponseDTO>(createdNotice);
    }

    public async Task<MessageResponseDTO> UpdateNoticeAsync(MessageRequestDTO message)
    {
        await _validator.ValidateAndThrowAsync(message);
        var noticeToUpdate = _mapper.Map<Message>(message);
        var updatedNotice = await _messsageRepository.UpdateAsync(noticeToUpdate)
                             ?? throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(message.Id));
        return _mapper.Map<MessageResponseDTO>(updatedNotice);
    }

    public async Task DeleteNoticeAsync(long id)
    {
        if (await _messsageRepository.DeleteAsync(id) is null)
        {
            throw new NotFoundException(ErrorCodes.NoticeNotFound, ErrorMessages.NoticeNotFoundMessage(id));
        }
    }
}