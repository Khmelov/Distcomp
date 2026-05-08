using RestSharp;
using Xunit;
using System.Net;
using Distcomp.Application.DTOs;

namespace Distcomp.Tests
{
    public class IssueApiTests
    {
        private readonly RestClient _client = new("http://localhost:24110/api/v1.0/");

        [Fact]
        public async Task CreateIssue_Success()
        {
            var newIssue = new
            {
                userId = 1,
                title = "Task from test",
                content = "This is a detailed description of the task"
            };

            var request = new RestRequest("issues", Method.Post);
            request.AddJsonBody(newIssue);

            var response = await _client.ExecuteAsync<IssueResponseTo>(request);

            Assert.Equal(HttpStatusCode.Created, response.StatusCode);
            Assert.NotNull(response.Data);
            Assert.Equal(newIssue.title, response.Data.Title);
        }

        [Fact]
        public async Task CreateIssue_UserNotFound_Returns404()
        {
            var invalidIssue = new
            {
                userId = 9999,
                title = "Ghost Task",
                content = "Valid long content"
            };

            var request = new RestRequest("issues", Method.Post);
            request.AddJsonBody(invalidIssue);

            var response = await _client.ExecuteAsync(request);

            Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
            Assert.Contains("40401", response.Content!);
        }
    }
}