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
	[Route("/api/v1.0/labels")]
	[ApiController]
	public class LabelController : ControllerBase
	{
		private readonly ILabelService _labelService;
		private readonly IMapper _mapper;

		public LabelController(ILabelService labelService, IMapper mapper)
		{
			_labelService = labelService;
			_mapper = mapper;
		}

		[HttpPost]
		public IActionResult CreateLabel(LabelRequestTo labelRequestDTO)
		{
			try
			{
				var labelResponseDTO = _labelService.CreateLabel(labelRequestDTO);
				return StatusCode(201, labelResponseDTO);
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
		public IActionResult GetAllLabels()
		{
			var labelsResponseDTO = _labelService.GetAllLabels();
			return Ok(labelsResponseDTO);
		}

		[HttpGet("{id}")]
		public IActionResult GetUserById(int id)
		{
			var labelResponseDTO = _labelService.GetLabelById(id);

			if (labelResponseDTO == null)
				return NotFound();

			return Ok(labelResponseDTO);
		}

		[HttpPut]
		public IActionResult UpdateLabel(LabelRequestTo labelRequestDTO)
		{
			try
			{
				var labelResponseDTO = _labelService.UpdateLabel(labelRequestDTO.Id, labelRequestDTO);
				if (labelResponseDTO == null)
					return NotFound();

				return Ok(labelResponseDTO);
			}
			catch (ArgumentException ex)
			{
				var errorMessage = ErrorResponse.CreateErrorResponse(ex.Message, System.Net.HttpStatusCode.BadRequest); ;

				return BadRequest(errorMessage);
			}
		}

		[HttpDelete("{id}")]
		public IActionResult DeleteLabel(int id)
		{
			var isDeleted = _labelService.DeleteLabel(id);

			if (!isDeleted)
				return NotFound();

			return NoContent();
		}
	}
}
