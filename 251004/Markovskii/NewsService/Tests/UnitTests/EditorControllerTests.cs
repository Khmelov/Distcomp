using Application.DTO.Request.Editor;
using Application.DTO.Response.Editor;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Moq;
using WebApi.Controllers;
using Xunit;

namespace Tests.UnitTests
{
    public class EditorControllerTests
    {
        private readonly Mock<IEditorService> _mockEditorService;
        private readonly EditorController _controller;

        public EditorControllerTests()
        {
            _mockEditorService = new Mock<IEditorService>();
            _controller = new EditorController(_mockEditorService.Object);
        }

        [Fact]
        public async Task GetAllEditors_Should_Return_OkResult_With_Editors()
        {
            // Arrange
            var editors = new List<EditorResponseToGetById>
            {
                new EditorResponseToGetById { Id = 1, Firstname = "John", Lastname = "Doe" },
                new EditorResponseToGetById { Id = 2, Firstname = "Jane", Lastname = "Doe" }
            };
            _mockEditorService.Setup(service => service.GetEditors(It.IsAny<EditorRequestToGetAll>()))
                .ReturnsAsync(editors);

            // Act
            var result = await _controller.GetAllEditors();

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsAssignableFrom<IEnumerable<EditorResponseToGetById>>(okResult.Value);
            Assert.Equal(2, returnValue.Count());
        }

        [Fact]
        public async Task GetEditorById_Should_Return_OkResult_With_Editor()
        {
            // Arrange
            var editor = new EditorResponseToGetById { Id = 1, Firstname = "John", Lastname = "Doe" };
            _mockEditorService.Setup(service => service.GetEditorById(It.IsAny<EditorRequestToGetById>()))
                .ReturnsAsync(editor);

            // Act
            var result = await _controller.GetEditorById(1);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<EditorResponseToGetById>(okResult.Value);
            Assert.Equal(editor.Id, returnValue.Id);
        }

        [Fact]
        public async Task CreateEditor_Should_Return_CreatedResult()
        {
            // Arrange
            var editorRequest = new EditorRequestToCreate { Firstname = "John", Lastname = "Doe" };
            var createdEditor = new EditorResponseToGetById { Id = 1, Firstname = "John", Lastname = "Doe" };
            _mockEditorService.Setup(service => service.CreateEditor(editorRequest))
                .ReturnsAsync(createdEditor);

            // Act
            var result = await _controller.CreateEditor(editorRequest);

            // Assert
            var createdResult = Assert.IsType<CreatedAtActionResult>(result);
            var returnValue = Assert.IsType<EditorResponseToGetById>(createdResult.Value);
            Assert.Equal(createdEditor.Id, returnValue.Id);
        }

        [Fact]
        public async Task UpdateEditor_Should_Return_OkResult_With_Updated_Editor()
        {
            // Arrange
            var editorModel = new EditorRequestToFullUpdate { Id = 1, Firstname = "John", Lastname = "Doe" };
            var updatedEditor = new EditorResponseToGetById { Id = 1, Firstname = "John Updated", Lastname = "Doe" };
            _mockEditorService.Setup(service => service.UpdateEditor(editorModel))
                .ReturnsAsync(updatedEditor);

            // Act
            var result = await _controller.UpdateEditor(editorModel);

            // Assert
            var okResult = Assert.IsType<OkObjectResult>(result);
            var returnValue = Assert.IsType<EditorResponseToGetById>(okResult.Value);
            Assert.Equal(updatedEditor.Firstname, returnValue.Firstname);
        }

        [Fact]
        public async Task DeleteEditor_Should_Return_NoContent()
        {
            // Arrange
            _mockEditorService.Setup(service => service.DeleteEditor(It.IsAny<EditorRequestToDeleteById>()))
                .ReturnsAsync(new EditorResponseToGetById { Id = 1 });

            // Act
            var result = await _controller.DeleteEditor(1);

            // Assert
            Assert.IsType<NoContentResult>(result);
        }
    }
}