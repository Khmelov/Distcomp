using Distcomp.Application.DTOs;
using RestSharp;
using System.Net;

namespace Distcomp.Tests
{
    public class NoteApiTests
    {
        private readonly RestClient _client = new("http://localhost:24110/api/v1.0/");

        [Fact]
        public async Task CreateNote_ForExistingIssue_Success()
        {
            var issueReq = new RestRequest("issues", Method.Post).AddJsonBody(new
            {
                userId = 1,
                title = "Issue for Note",
                content = "content"
            });
            var issueRes = await _client.ExecuteAsync<IssueResponseTo>(issueReq);
            long issueId = issueRes.Data!.Id;

            var noteReq = new RestRequest("notes", Method.Post).AddJsonBody(new
            {
                issueId = issueId,
                content = "This is a note"
            });
            var noteRes = await _client.ExecuteAsync<NoteResponseTo>(noteReq);

            Assert.Equal(HttpStatusCode.Created, noteRes.StatusCode);
            Assert.Equal("This is a note", noteRes.Data!.Content);
        }
    }
}