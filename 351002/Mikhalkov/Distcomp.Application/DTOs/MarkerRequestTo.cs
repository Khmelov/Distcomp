using System.ComponentModel.DataAnnotations;

namespace Distcomp.Application.DTOs
{
    public record MarkerRequestTo(
        long? Id,
        string Name
    );
}
