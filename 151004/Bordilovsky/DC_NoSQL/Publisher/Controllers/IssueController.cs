using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;
using DC_REST.Services.Interfaces;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Net;

namespace DC_REST.Controllers
{
	[Route("/api/v1.0/issues")]
	[ApiController]
	public class IssueController : ControllerBase
	{
		private readonly IIssueService _issueService;
		private readonly IMapper _mapper;

		public IssueController(IIssueService issueService, IMapper mapper)
		{
			_issueService = issueService;
			_mapper = mapper;
		}

		[HttpPost]
		public IActionResult CreateIssue(IssueRequestTo issueRequestDTO)
		{
			try
			{
				var issueResponseDTO = _issueService.CreateIssue(issueRequestDTO);
				return StatusCode(201, issueResponseDTO);
			}
			catch (DbUpdateException ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, HttpStatusCode.BadRequest); ;
				return StatusCode(403, errorMessage);
			}
			catch (Exception ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, HttpStatusCode.BadRequest); ;
				return BadRequest(errorMessage);
			}
		}

		[HttpGet]
		public IActionResult GetAllIssues()
		{
			var issuesResponseDTO = _issueService.GetAllIssues();
			return Ok(issuesResponseDTO);
		}

		[HttpGet("{id}")]
		public IActionResult GetIssueById(int id)
		{
			var issueResponseDTO = _issueService.GetIssueById(id);

			if (issueResponseDTO == null)
				return NotFound();

			return Ok(issueResponseDTO);
		}

		[HttpPut]
		public IActionResult UpdateIssue(IssueRequestTo issueRequestDTO)
		{
			try
			{
				var issueResponseDTO = _issueService.UpdateIssue(issueRequestDTO.Id, issueRequestDTO);
				if (issueResponseDTO == null)
					return NotFound();

				return Ok(issueResponseDTO);
			}
			catch (ArgumentException ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, HttpStatusCode.BadRequest); ;

				return BadRequest(errorMessage);
			}
		}

		[HttpDelete("{id}")]
		public IActionResult DeleteIssue(int id)
		{
			var isDeleted = _issueService.DeleteIssue(id);

			if (!isDeleted)
				return NotFound();

			return NoContent();
		}
	}
}
