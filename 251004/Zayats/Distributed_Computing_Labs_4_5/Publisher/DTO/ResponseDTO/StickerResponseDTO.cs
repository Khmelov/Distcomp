using Publisher.Models;

namespace Publisher.DTO.ResponseDTO;

public class StickerResponseDTO
{
    public long Id { get; set; }
    public string Name { get; set; }
    
    public List<Topic> Stories { get; set; } = [];
}