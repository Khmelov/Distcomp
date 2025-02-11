using Application.DTO.Request.Editor;
using Application.DTO.Request.News;
using Application.DTO.Request.Post;
using Application.DTO.Response.Editor;
using Application.DTO.Response.News;
using Application.DTO.Response.Post;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Moq;
using WebApi.Controllers;
using Xunit;

namespace Tests.UnitTests
{
    public class NewsControllerTests
    {
        private readonly Mock<INewsService> _mockNewsService;
        private readonly Mock<IEditorService> _mockEditorService;
        private readonly Mock<IPostService> _mockPostService;
        private readonly NewsController _controller;

        public NewsControllerTests()
        {
            _mockNewsService = new Mock<INewsService>();
            _mockEditorService = new Mock<IEditorService>();
            _mockPostService = new Mock<IPostService>();
            _controller = new NewsController(_mockNewsService.Object, _mockEditorService.Object, _mockPostService.Object);
        }

        [Fact]
        public async Task GetAllNews_Should_Return_OkResult_With_News()
        {
            // Arrange
            var newsList = new List<NewsResponseToGetById>
            {
                new NewsResponseToGetById { Id = 1, Title = "First News" },
                new NewsResponseToGetById { Id = 2, Title = "Second News" }
            };
            _mockNewsService.Setup(service => service.GetNews(It.IsAny<NewsRequestToGetAll>()))
                .ReturnsAsync(newsList);

            // Act
            var result = await _controller.GetAllNews();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsAssignableFrom<IEnumerable<NewsResponseToGetById>>(okResult.Value);
            Assert.Equal(2, returnValue.Count());
        }

        [Fact]
        public async Task GetEditorByNewsId_Should_Return_OkResult_With_Editor()
        {
            // Arrange
            var editor = new EditorResponseToGetById { Id = 1, Login="dsfs", Lastname = "21", Firstname = "Editor Name" };
            _mockEditorService.Setup(service => service.GetEditorByNewsId(It.IsAny<EditorRequestToGetByNewsId>()))
                .ReturnsAsync(editor);

            // Act
            var result = await _controller.GetEditorByNewsId(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<EditorResponseToGetById>(okResult.Value);
            Assert.Equal(editor.Id, returnValue.Id);
        }

        [Fact]
        public async Task GetPostsByNewsId_Should_Return_OkResult_With_Posts()
        {
            // Arrange
            var posts = new List<PostResponseToGetById>
            {
                new PostResponseToGetById { Id = 1, Content = "First Post" },
                new PostResponseToGetById { Id = 2, Content = "Second Post" }
            };
            _mockPostService.Setup(service => service.GetPostsByNewsId(It.IsAny<PostRequestToGetByNewsId>()))
                .ReturnsAsync(posts);

            // Act
            var result = await _controller.GetPostsByNewsId(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsAssignableFrom<IEnumerable<PostResponseToGetById>>(okResult.Value);
            Assert.Equal(2, returnValue.Count());
        }

        [Fact]
        public async Task GetNewsById_Should_Return_OkResult_With_News()
        {
            // Arrange
            var news = new NewsResponseToGetById { Id = 1, Title = "First News" };
            _mockNewsService.Setup(service => service.GetNewsById(It.IsAny<NewsRequestToGetById>()))
                .ReturnsAsync(news);

            // Act
            var result = await _controller.GetNewsById(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<NewsResponseToGetById>(okResult.Value);
            Assert.Equal(news.Id, returnValue.Id);
        }

        [Fact]
        public async Task CreateNews_Should_Return_CreatedResult()
        {
            // Arrange
            var newsRequest = new NewsRequestToCreate { Title = "New News" };
            var createdNews = new NewsResponseToGetById { Id = 1, Title = "New News" };
            _mockNewsService.Setup(service => service.CreateNews(newsRequest))
                .ReturnsAsync(createdNews);

            // Act
            var result = await _controller.CreateNews(newsRequest);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result);
            var returnValue = Assert.IsType<NewsResponseToGetById>(createdResult.Value);
            Assert.Equal(createdNews.Id, returnValue.Id);
        }

        [Fact]
        public async Task UpdateNews_Should_Return_OkResult_With_Updated_News()
        {
            // Arrange
            var newsModel = new NewsRequestToFullUpdate { Id = 1, Title = "Updated News" };
            var updatedNews = new NewsResponseToGetById { Id = 1, Title = "Updated News" };
            _mockNewsService.Setup(service => service.UpdateNews(newsModel))
                .ReturnsAsync(updatedNews);

            // Act
            var result = await _controller.UpdateNews(newsModel);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<NewsResponseToGetById>(okResult.Value);
            Assert.Equal(updatedNews.Title, returnValue.Title);
        }

        [Fact]
        public async Task DeleteNews_Should_Return_NoContent()
        {
            // Arrange
            _mockNewsService.Setup(service => service.DeleteNews(It.IsAny<NewsRequestToDeleteById>()))
                .ReturnsAsync(new NewsResponseToGetById { Id = 1 });

            // Act
            var result = await _controller.DeleteNews(1);

            // Assert
            Assert.IsType<NoContentResult>(result);
        }
    }
}