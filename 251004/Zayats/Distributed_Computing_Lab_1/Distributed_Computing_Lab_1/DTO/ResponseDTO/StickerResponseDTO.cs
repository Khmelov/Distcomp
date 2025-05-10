using Distributed_Computing_Lab_1.Models;

namespace Distributed_Computing_Lab_1.DTO.ResponseDTO;

public class StickerResponseDTO
{
    public long Id { get; set; }
    public string Name { get; set; }
    
    public List<Topic> Stories { get; set; } = [];
}