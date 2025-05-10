using Publisher.DTO.RequestDTO;
using Publisher.DTO.ResponseDTO;

namespace Publisher.Services.Interfaces;

public interface IStickerService
{
    Task<IEnumerable<StickerResponseDTO>> GetTagsAsync();

    Task<StickerResponseDTO> GetTagByIdAsync(long id);

    Task<StickerResponseDTO> CreateTagAsync(StickerRequestDTO sticker);

    Task<StickerResponseDTO> UpdateTagAsync(StickerRequestDTO sticker);

    Task DeleteTagAsync(long id);
}