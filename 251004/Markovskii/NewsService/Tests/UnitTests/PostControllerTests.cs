using Application.DTO.Request.Post;
using Application.DTO.Response.Post;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Moq;
using WebApi.Controllers;
using Xunit;

namespace Tests.UnitTests
{
    public class PostControllerTests
    {
        private readonly Mock<IPostService> _mockPostService;
        private readonly PostController _controller;

        public PostControllerTests()
        {
            _mockPostService = new Mock<IPostService>();
            _controller = new PostController(_mockPostService.Object);
        }

        [Fact]
        public async Task GetAllPost_Should_Return_OkResult_With_Posts()
        {
            // Arrange
            var posts = new List<PostResponseToGetById>
            {
                new PostResponseToGetById { Id = 1, Content = "First Post" },
                new PostResponseToGetById { Id = 2,Content = "Second Post" }
            };
            _mockPostService.Setup(service => service.GetPosts(It.IsAny<PostRequestToGetAll>()))
                .ReturnsAsync(posts);

            // Act
            var result = await _controller.GetAllPost();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsAssignableFrom<IEnumerable<PostResponseToGetById>>(okResult.Value);
            Assert.Equal(2, returnValue.Count());
        }

        [Fact]
        public async Task GetPostById_Should_Return_OkResult_With_Post()
        {
            // Arrange
            var post = new PostResponseToGetById { Id = 1, Content = "First Post" };
            _mockPostService.Setup(service => service.GetPostById(It.IsAny<PostRequestToGetById>()))
                .ReturnsAsync(post);

            // Act
            var result = await _controller.GetPostById(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<PostResponseToGetById>(okResult.Value);
            Assert.Equal(post.Id, returnValue.Id);
        }

        [Fact]
        public async Task CreatePost_Should_Return_CreatedResult()
        {
            // Arrange
            var postRequest = new PostRequestToCreate { Content = "New Post" };
            var createdPost = new PostResponseToGetById { Id = 1, Content = "New Post" };
            _mockPostService.Setup(service => service.CreatePost(postRequest))
                .ReturnsAsync(createdPost);

            // Act
            var result = await _controller.CreatePost(postRequest);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result);
            var returnValue = Assert.IsType<PostResponseToGetById>(createdResult.Value);
            Assert.Equal(createdPost.Id, returnValue.Id);
        }

        [Fact]
        public async Task UpdatePost_Should_Return_OkResult_With_Updated_Post()
        {
            // Arrange
            var postModel = new PostRequestToFullUpdate { Id = 1, Content = "Updated Post" };
            var updatedPost = new PostResponseToGetById { Id = 1, Content = "Updated Post" };
            _mockPostService.Setup(service => service.UpdatePost(postModel))
                .ReturnsAsync(updatedPost);

            // Act
            var result = await _controller.UpdatePost(postModel);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<PostResponseToGetById>(okResult.Value);
            Assert.Equal(updatedPost.Content, returnValue.Content);
        }

        [Fact]
        public async Task DeletePost_Should_Return_NoContent()
        {
            // Arrange
            _mockPostService.Setup(service => service.DeletePost(It.IsAny<PostRequestToDeleteById>()))
                .ReturnsAsync(new PostResponseToGetById { Id = 1 });

            // Act
            var result = await _controller.DeletePost(1);

            // Assert
            Assert.IsType<NoContentResult>(result);
        }
    }
}