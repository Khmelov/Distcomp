using Publisher.Domain.Models;

namespace Publisher.Application;

public interface IJwtGenerator
{
    string GetJwt(User user);
}
