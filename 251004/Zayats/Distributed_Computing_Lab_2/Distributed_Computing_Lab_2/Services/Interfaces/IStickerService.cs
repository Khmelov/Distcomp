using Distributed_Computing_Lab_2.DTO.RequestDTO;
using Distributed_Computing_Lab_2.DTO.ResponseDTO;

namespace Distributed_Computing_Lab_2.Services.Interfaces;

public interface IStickerService
{
    
    Task<IEnumerable<StickerResponseDTO>> GetTagsAsync();

    Task<StickerResponseDTO> GetTagByIdAsync(long id);

    Task<StickerResponseDTO> CreateTagAsync(StickerRequestDTO sticker);

    Task<StickerResponseDTO> UpdateTagAsync(StickerRequestDTO sticker);

    Task DeleteTagAsync(long id);
}