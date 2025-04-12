using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;

public abstract class BaseEntity
{
    public int id { get; set; }
}

public class Editor : BaseEntity
{
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Логин должен содержать не менее 2 символов.")]
    public string login { get; set; }

    [Required]
    [MaxLength(128)]
    [MinLength(8, ErrorMessage = "Пароль должен содержать не менее 8 символов.")]
    public string password { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Имя должно содержать не менее 2 символов.")]
    public string firstname { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Фамилия должна содержать не менее 2 символов.")]
    public string lastname { get; set; }
}

public class Story : BaseEntity
{
    [Required]
    public int EditorId { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Заголовок должен содержать не менее 2 символов.")]
    public string Title { get; set; }

    [Required]
    [MaxLength(2048)]
    [MinLength(4, ErrorMessage = "Содержание должно содержать не менее 4 символов.")]
    public string Content { get; set; }

    [Required]
    public DateTime Created { get; set; }

    [Required]
    public DateTime Modified { get; set; }
}

public class Label : BaseEntity
{
    [Required]
    [MaxLength(32)]
    [MinLength(2, ErrorMessage = "Название должно содержать не менее 2 символов.")]
    public string Name { get; set; }
}

public class Note : BaseEntity
{
    [Required]
    public int storyId { get; set; }

    [Required]
    [MaxLength(2048)]
    [MinLength(2, ErrorMessage = "Содержание должно содержать не менее 2 символов.")]
    public string Content { get; set; }
}

public class EditorRequestTo
{
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Логин должен содержать не менее 2 символов.")]
    public string Login { get; set; }

    [Required]
    [MaxLength(128)]
    [MinLength(8, ErrorMessage = "Пароль должен содержать не менее 8 символов.")]
    public string Password { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Имя должно содержать не менее 2 символов.")]
    public string Firstname { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Фамилия должна содержать не менее 2 символов.")]
    public string Lastname { get; set; }
}

public class StoryRequestTo
{
    [Required]
    public int EditorId { get; set; }

    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Заголовок должен содержать не менее 2 символов.")]
    public string Title { get; set; }

    [Required]
    [MaxLength(2048)]
    [MinLength(4, ErrorMessage = "Содержание должно содержать не менее 4 символов.")]
    public string Content { get; set; }

    [Required]
    public DateTime Created { get; set; }

    [Required]
    public DateTime Modified { get; set; }
}

public class LabelRequestTo
{
    [Required]
    [MaxLength(32)]
    [MinLength(2, ErrorMessage = "Название должно содержать не менее 2 символов.")]
    public string Name { get; set; }
}

public class NoteRequestTo
{
    [Required]
    [MaxLength(2048)]
    [MinLength(2, ErrorMessage = "Содержание должно содержать не менее 2 символов.")]
    public string Content { get; set; }

    [Required]
    public int StoryId { get; set; }
}

[JsonObject("editor")]
public class EditorResponseTo
{
    [JsonProperty("id")]
    public int Id { get; set; }

    [JsonProperty("login")]
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Логин должен содержать не менее 2 символов.")]
    public string Login { get; set; }

    [JsonProperty("password")]
    [Required]
    [MaxLength(128)]
    [MinLength(8, ErrorMessage = "Пароль должен содержать не менее 8 символов.")]
    public string Password { get; set; }

    [JsonProperty("firstname")]
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Имя должно содержать не менее 2 символов.")]
    public string Firstname { get; set; }

    [JsonProperty("lastname")]
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Фамилия должна содержать не менее 2 символов.")]
    public string Lastname { get; set; }
}

[JsonObject("story")]
public class StoryResponseTo
{
    [JsonProperty("id")]
    public int Id { get; set; }

    [JsonProperty("title")]
    [Required]
    [MaxLength(64)]
    [MinLength(2, ErrorMessage = "Заголовок должен содержать не менее 2 символов.")]
    public string Title { get; set; }

    [JsonProperty("content")]
    [Required]
    [MaxLength(2048)]
    [MinLength(4, ErrorMessage = "Содержание должно содержать не менее 4 символов.")]
    public string Content { get; set; }

    [JsonProperty("editorId")]
    [Required]
    public int EditorId { get; set; }

    [JsonProperty("created")]
    [Required]
    public DateTime created { get; set; }

    [JsonProperty("modified")]
    [Required]
    public DateTime modified { get; set; }
}

[JsonObject("label")]
public class LabelResponseTo
{
    [JsonProperty("id")]
    public int Id { get; set; }

    [JsonProperty("name")]
    [Required]
    [MaxLength(32)]
    [MinLength(2, ErrorMessage = "Название должно содержать не менее 2 символов.")]
    public string Name { get; set; }
}

[JsonObject("note")]
public class NoteResponseTo
{
    [JsonProperty("id")]
    public int Id { get; set; }

    [JsonProperty("content")]
    [Required]
    [MaxLength(2048)]
    [MinLength(2, ErrorMessage = "Содержание должно содержать не менее 2 символов.")]
    public string Content { get; set; }

    [JsonProperty("storyId")]
    [Required]
    public int StoryId { get; set; }
}