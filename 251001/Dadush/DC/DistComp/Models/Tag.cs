using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;

namespace DistComp.Models {

    [Table("tbl_tag")]
    public class Tag : EntityWithId {

        [Column("name")]
        [Length(2, 32)]
        public string Name { get; set; } = null!;

        [JsonIgnore]
        public ICollection<Topic> Topics { get; set; } = [];
    }

    public class TagInDto {

        [Length(2, 32)]
        public string Name { get; init; } = null!;
    }

    public class TagOutDto {

        public long Id { get; init; }

        public string Name { get; init; } = null!;
    }
}
