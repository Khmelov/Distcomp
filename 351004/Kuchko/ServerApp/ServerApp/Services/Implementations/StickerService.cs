using Mapster;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Models.Entities;
using ServerApp.Repository;
using ServerApp.Services.Interfaces;

namespace ServerApp.Services.Implementations;

public class StickerService(IRepository<Sticker> repository) : IStickerService
{
    public IEnumerable<StickerResponseTo> GetAll() => repository.GetAll().Adapt<IEnumerable<StickerResponseTo>>();

    public StickerResponseTo GetById(long id)
    {
        var sticker = repository.GetById(id) ?? throw new KeyNotFoundException($"Sticker {id} not found");
        return sticker.Adapt<StickerResponseTo>();
    }

    public StickerResponseTo Create(StickerRequestTo request)
    {
        var sticker = request.Adapt<Sticker>();
        var created = repository.Create(sticker);
        return created.Adapt<StickerResponseTo>();
    }

    public StickerResponseTo Update(long id, StickerRequestTo request)
    {
        var existing = repository.GetById(id) ?? throw new KeyNotFoundException($"Sticker {id} not found");
        request.Adapt(existing);
        existing.Id = id;
        repository.Update(existing);
        return existing.Adapt<StickerResponseTo>();
    }

    public void Delete(long id)
    {
        if (!repository.Delete(id)) throw new KeyNotFoundException($"Sticker {id} not found");
    }
}