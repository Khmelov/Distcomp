using Application.DTO.Request.Mark;
using Application.DTO.Response.Mark;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Moq;
using WebApi.Controllers;
using Xunit;

namespace Tests.UnitTests
{
    public class MarkControllerTests
    {
        private readonly Mock<IMarkService> _mockMarkService;
        private readonly MarkController _controller;

        public MarkControllerTests()
        {
            _mockMarkService = new Mock<IMarkService>();
            _controller = new MarkController(_mockMarkService.Object);
        }

        [Fact]
        public async Task GetAllMark_Should_Return_OkResult_With_Marks()
        {
            // Arrange
            var marks = new List<MarkResponseToGetById>
            {
                new MarkResponseToGetById { Id = 1, Name = "Excellent" },
                new MarkResponseToGetById { Id = 2, Name = "Good" }
            };
            _mockMarkService.Setup(service => service.GetMarks(It.IsAny<MarkRequestToGetAll>()))
                .ReturnsAsync(marks);

            // Act
            var result = await _controller.GetAllMark();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsAssignableFrom<IEnumerable<MarkResponseToGetById>>(okResult.Value);
            Assert.Equal(2, returnValue.Count());
        }

        [Fact]
        public async Task GetMarkById_Should_Return_OkResult_With_Mark()
        {
            // Arrange
            var mark = new MarkResponseToGetById { Id = 1, Name = "Excellent" };
            _mockMarkService.Setup(service => service.GetMarkById(It.IsAny<MarkRequestToGetById>()))
                .ReturnsAsync(mark);

            // Act
            var result = await _controller.GetMarkById(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<MarkResponseToGetById>(okResult.Value);
            Assert.Equal(mark.Id, returnValue.Id);
        }

        [Fact]
        public async Task CreateMark_Should_Return_CreatedResult()
        {
            // Arrange
            var markRequest = new MarkRequestToCreate { Name = "Excellent" };
            var createdMark = new MarkResponseToGetById { Id = 1, Name = "Excellent" };
            _mockMarkService.Setup(service => service.CreateMark(markRequest))
                .ReturnsAsync(createdMark);

            // Act
            var result = await _controller.CreateMark(markRequest);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result);
            var returnValue = Assert.IsType<MarkResponseToGetById>(createdResult.Value);
            Assert.Equal(createdMark.Id, returnValue.Id);
        }

        [Fact]
        public async Task UpdateMark_Should_Return_OkResult_With_Updated_Mark()
        {
            // Arrange
            var markModel = new MarkRequestToFullUpdate { Id = 1, Name = "Updated Mark" };
            var updatedMark = new MarkResponseToGetById { Id = 1, Name = "Updated Mark" };
            _mockMarkService.Setup(service => service.UpdateMark(markModel))
                .ReturnsAsync(updatedMark);

            // Act
            var result = await _controller.UpdateMark(markModel);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<MarkResponseToGetById>(okResult.Value);
            Assert.Equal(updatedMark.Name, returnValue.Name);
        }

        [Fact]
        public async Task DeleteMark_Should_Return_NoContent()
        {
            // Arrange
            _mockMarkService.Setup(service => service.DeleteMark(It.IsAny<MarkRequestToDeleteById>()))
                .ReturnsAsync(new MarkResponseToGetById { Id = 1 });

            // Act
            var result = await _controller.DeleteMark(1);

            // Assert
            Assert.IsType<NoContentResult>(result);
        }
    }
}