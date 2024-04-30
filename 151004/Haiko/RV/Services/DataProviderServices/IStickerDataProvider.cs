using RV.Views.DTO;

namespace RV.Services.DataProviderServices
{
    public interface IStickerDataProvider
    {
        StickerDTO CreateSticker(StickerAddDTO item);
        List<StickerDTO> GetStickers();
        StickerDTO GetSticker(int id);
        StickerDTO UpdateSticker(StickerUpdateDTO item);
        int DeleteSticker(int id);
    }
}
