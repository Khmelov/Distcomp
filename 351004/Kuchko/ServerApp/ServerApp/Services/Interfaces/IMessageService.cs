using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;

namespace ServerApp.Services.Interfaces;

public interface IMessageService
{
    IEnumerable<MessageResponseTo> GetAll();
    MessageResponseTo GetById(long id);
    MessageResponseTo Create(MessageRequestTo request);
    MessageResponseTo Update(long id, MessageRequestTo request);
    void Delete(long id);
}