using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.DTO.ResponseDTO;

namespace Distributed_Computing_Lab_1.Services.Interfaces;

public interface IStickerService
{
    Task<IEnumerable<StickerResponseDTO>> GetTagsAsync();

    Task<StickerResponseDTO> GetTagByIdAsync(long id);

    Task<StickerResponseDTO> CreateTagAsync(StickerRequestDTO sticker);

    Task<StickerResponseDTO> UpdateTagAsync(StickerRequestDTO sticker);

    Task DeleteTagAsync(long id);
}