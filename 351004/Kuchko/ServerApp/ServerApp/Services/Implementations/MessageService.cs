using Mapster;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class MessageService(
    IRepository<Message> messageRepo, 
    IRepository<Article> articleRepo) : IMessageService
{
    public IEnumerable<MessageResponseTo> GetAll() => messageRepo.GetAll().Adapt<IEnumerable<MessageResponseTo>>();

    public MessageResponseTo GetById(long id)
    {
        var msg = messageRepo.GetById(id) ?? throw new KeyNotFoundException($"Message {id} not found");
        return msg.Adapt<MessageResponseTo>();
    }

    public MessageResponseTo Create(MessageRequestTo request)
    {
        if (articleRepo.GetById(request.ArticleId) == null)
            throw new ArgumentException($"Article {request.ArticleId} not found");

        var message = request.Adapt<Message>();
        var created = messageRepo.Create(message);
        return created.Adapt<MessageResponseTo>();
    }

    public MessageResponseTo Update(long id, MessageRequestTo request)
    {
        var existing = messageRepo.GetById(id) ?? throw new KeyNotFoundException($"Message {id} not found");
        if (articleRepo.GetById(request.ArticleId) == null)
            throw new ArgumentException($"Article {request.ArticleId} not found");

        request.Adapt(existing);
        existing.Id = id;
        messageRepo.Update(existing);
        return existing.Adapt<MessageResponseTo>();
    }

    public void Delete(long id)
    {
        if (!messageRepo.Delete(id)) throw new KeyNotFoundException($"Message {id} not found");
    }
}