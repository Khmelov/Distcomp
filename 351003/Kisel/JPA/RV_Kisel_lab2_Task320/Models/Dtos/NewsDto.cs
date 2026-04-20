using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace RV_Kisel_lab2_Task320.Models.Dtos;

// Магический конвертер, который учит программу читать массив строк ["red", "green"] как список меток
public class LabelListConverter : JsonConverter<List<LabelDto>>
{
    public override List<LabelDto> ReadJson(JsonReader reader, Type objectType, List<LabelDto>? existingValue, bool hasExistingValue, JsonSerializer serializer)
    {
        var list = new List<LabelDto>();
        if (reader.TokenType == JsonToken.StartArray)
        {
            JArray array = JArray.Load(reader);
            foreach (var item in array)
            {
                // Если пришла просто строка (как в тестах)
                if (item.Type == JTokenType.String)
                {
                    list.Add(new LabelDto { Name = item.ToString() });
                }
                // Если пришел нормальный объект
                else if (item.Type == JTokenType.Object)
                {
                    list.Add(item.ToObject<LabelDto>());
                }
            }
        }
        return list;
    }

    public override void WriteJson(JsonWriter writer, List<LabelDto>? value, JsonSerializer serializer)
    {
        serializer.Serialize(writer, value);
    }
}

public class NewsDto 
{
    public int Id { get; set; }

    [Required]
    [StringLength(64, MinimumLength = 2)]
    public string Title { get; set; } = string.Empty;

    [Required]
    [StringLength(2048, MinimumLength = 4)]
    public string Content { get; set; } = string.Empty;

    public int CreatorId { get; set; }

    // Указываем программе использовать наш конвертер
    [JsonConverter(typeof(LabelListConverter))]
    public List<LabelDto> Labels { get; set; } = new();
}