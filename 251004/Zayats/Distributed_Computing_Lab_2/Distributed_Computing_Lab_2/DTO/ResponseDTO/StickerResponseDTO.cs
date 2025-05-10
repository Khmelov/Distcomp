using Distributed_Computing_Lab_2.Models;

namespace Distributed_Computing_Lab_2.DTO.ResponseDTO;

public class StickerResponseDTO
{
    public long Id { get; set; }
    public string Name { get; set; }
    
    public List<Topic> Stories { get; set; } = [];
}