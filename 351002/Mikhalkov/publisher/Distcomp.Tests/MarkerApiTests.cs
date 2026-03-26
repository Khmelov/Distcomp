using RestSharp;
using System.Net;

namespace Distcomp.Tests
{
    public class MarkerApiTests
    {
        private readonly RestClient _client = new("http://localhost:24110/api/v1.0/");

        [Fact]
        public async Task CreateDuplicateMarker_Returns403()
        {
            var marker = new { name = "Bug" };

            var req1 = new RestRequest("markers", Method.Post).AddJsonBody(marker);
            await _client.ExecuteAsync(req1);

            var req2 = new RestRequest("markers", Method.Post).AddJsonBody(marker);
            var response = await _client.ExecuteAsync(req2);

            Assert.Equal(HttpStatusCode.Forbidden, response.StatusCode);
            Assert.Contains("40302", response.Content!);
        }
    }
}