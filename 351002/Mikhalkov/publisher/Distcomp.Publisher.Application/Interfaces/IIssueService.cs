using Distcomp.Application.DTOs;

namespace Distcomp.Application.Interfaces
{
    public interface IIssueService
    {
        IssueResponseTo Create(IssueRequestTo request);
        IssueResponseTo? GetById(long id);
        IEnumerable<IssueResponseTo> GetAll();
        IssueResponseTo Update(long id, IssueRequestTo request);
        bool Delete(long id);
    }
}