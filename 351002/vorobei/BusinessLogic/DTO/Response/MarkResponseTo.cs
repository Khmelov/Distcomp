using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using DataAccess.Models;

namespace BusinessLogic.DTO.Response
{
    public class MarkResponseTo : BaseEntity
    {
        [JsonPropertyName("name")]
        public string Name { get; set; } = string.Empty;
    }
}
