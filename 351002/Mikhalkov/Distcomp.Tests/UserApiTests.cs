using RestSharp;
using System.Net;
using Distcomp.Application.DTOs;

namespace Distcomp.Tests
{
    public class UserApiTests
    {
        private readonly RestClient _client;

        public UserApiTests()
        {
            _client = new RestClient("http://localhost:24110/api/v1.0/");
        }

        [Fact]
        public async Task CreateUser_And_GetById_ShouldReturnCorrectData()
        {
            var newUser = new
            {
                login = "test_user_" + Guid.NewGuid().ToString().Substring(0, 5),
                password = "securePassword123",
                firstname = "Ivan",
                lastname = "Ivanov"
            };

            var postRequest = new RestRequest("users", Method.Post);
            postRequest.AddJsonBody(newUser);
            var postResponse = await _client.ExecuteAsync<UserResponseTo>(postRequest);

            Assert.Equal(HttpStatusCode.Created, postResponse.StatusCode);
            var createdUser = postResponse.Data;
            Assert.NotNull(createdUser);
            Assert.Equal(newUser.login, createdUser.Login);

            var getRequest = new RestRequest($"users/{createdUser.Id}", Method.Get);
            var getResponse = await _client.ExecuteAsync<UserResponseTo>(getRequest);

            Assert.Equal(HttpStatusCode.OK, getResponse.StatusCode);
            Assert.Equal("Ivan", getResponse.Data?.FirstName);
        }

        [Fact]
        public async Task CreateUser_WithShortLogin_ShouldReturn400()
        {
            var invalidUser = new
            {
                login = "x",
                password = "password",
                firstname = "A",
                lastname = "B"
            };

            var request = new RestRequest("users", Method.Post);
            request.AddJsonBody(invalidUser);

            var response = await _client.ExecuteAsync(request);

            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
            Assert.Contains("40001", response.Content!);
        }
    }
}