using Distcomp.Application.DTOs;

namespace Distcomp.Application.Interfaces
{
    public interface IAuthService
    {
        AuthResponseTo Login(AuthRequestTo request);
    }
}