using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.Entities;
using DC_REST.Services.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Net;

namespace DC_REST.Controllers
{
	[Route("/api/v1.0/users")]
	[ApiController]
	public class UserController : ControllerBase
	{
		private readonly IUserService _userService;
		private readonly IMapper _mapper;

		public UserController(IUserService issueService, IMapper mapper)
		{
			_userService = issueService;
			_mapper = mapper;
		}

		[HttpPost]
		public IActionResult CreateUser(UserRequestTo userRequestDTO)
		{
			try
			{
				var userResponseDTO = _userService.CreateUser(userRequestDTO);
				return StatusCode(201, userResponseDTO);
			}
			catch (DbUpdateException ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, HttpStatusCode.BadRequest); ;
				return StatusCode(403, errorMessage);
			}
			catch (Exception ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, System.Net.HttpStatusCode.BadRequest); ;
				return BadRequest(errorMessage);
			}
		}

		[HttpGet]
		public IActionResult GetAllUsers()
		{
			var usersResponseDTO = _userService.GetAllUsers();
			return Ok(usersResponseDTO);
		}

		[HttpGet("{id}")]
		public IActionResult GetUserById(int id)
		{
			var userResponseDTO = _userService.GetUserById(id);

			if (userResponseDTO == null)
				return NotFound();

			return Ok(userResponseDTO);
		}

		[HttpPut]
		public IActionResult UpdateIssue(UserRequestTo userRequestDTO)
		{
			try
			{
				var userResponseDTO = _userService.UpdateUser(userRequestDTO.Id, userRequestDTO);
				if (userResponseDTO == null)
					return NotFound();

				return Ok(userResponseDTO);
			}
			catch (ArgumentException ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, System.Net.HttpStatusCode.BadRequest); ;
				return BadRequest(errorMessage);
			}
		}

		[HttpDelete("{id}")]
		public IActionResult DeleteUser(int id)
		{
			var isDeleted = _userService.DeleteUser(id);

			if (!isDeleted)
				return NotFound();

			return NoContent();
		}
	}
}
