using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;

namespace DC_REST.Services.Interfaces
{
    public interface IIssueService
    {
        IssueResponseTo CreateIssue(IssueRequestTo issueRequestDto);
        IssueResponseTo GetIssueById(int id);
        List<IssueResponseTo> GetAllIssues();
        IssueResponseTo UpdateIssue(int id, IssueRequestTo issueRequestDto);
        bool DeleteIssue(int id);
    }
}
