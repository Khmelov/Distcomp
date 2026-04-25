using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;

namespace ServerApp.Services.Interfaces;

public interface IStickerService
{
    IEnumerable<StickerResponseTo> GetAll();
    StickerResponseTo GetById(long id);
    StickerResponseTo Create(StickerRequestTo request);
    StickerResponseTo Update(long id, StickerRequestTo request);
    void Delete(long id);
}