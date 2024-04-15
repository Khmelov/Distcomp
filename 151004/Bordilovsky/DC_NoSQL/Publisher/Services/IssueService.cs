using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;
using DC_REST.Repositories;
using DC_REST.Services.Interfaces;
using DC_REST.Validators;
using System.Collections.Generic;

namespace DC_REST.Services
{
	public class IssueService : IIssueService
	{
		private readonly IRepository<Issue> _issueRepository;
		private readonly IMapper _mapper;
		private readonly IValidator<IssueRequestTo> _issueValidator;

		public IssueService(IRepository<Issue> issueRepository, IMapper mapper, IValidator<IssueRequestTo> issueValidator)
		{
			_issueRepository = issueRepository;
			_mapper = mapper;
			_issueValidator = issueValidator;
		}

		public IssueResponseTo CreateIssue(IssueRequestTo issueRequestDto)
		{
			if (!_issueValidator.Validate(issueRequestDto))
			{
				throw new ArgumentException("Invalid issue data");
			}
			var issue = _mapper.Map<Issue>(issueRequestDto);
			//var currentId = _issueRepository.GetCurrentId();
			//issue.Id = currentId;
			var createdIssue = _issueRepository.Add(issue);
			var responseDto = _mapper.Map<IssueResponseTo>(createdIssue);

			return responseDto;
		}

		public IssueResponseTo GetIssueById(int id)
		{
			var issue = _issueRepository.GetById(id);
			var issueDto = _mapper.Map<IssueResponseTo>(issue);

			return issueDto;
		}

		public List<IssueResponseTo> GetAllIssues()
		{
			var issues = _issueRepository.GetAll();
			var issueDtos = _mapper.Map<List<IssueResponseTo>>(issues);

			return issueDtos;
		}

		public IssueResponseTo UpdateIssue(int id, IssueRequestTo issueRequestDTO)
		{
			if (!_issueValidator.Validate(issueRequestDTO))
			{
				throw new ArgumentException("Invalid issue data");
			}

			var existingIssue = _issueRepository.GetById(id);
			if (existingIssue == null)
			{
				return null;
			}

			_mapper.Map(issueRequestDTO, existingIssue);
			var updatedIssue = _issueRepository.Update(id, existingIssue);
			var responseDto = _mapper.Map<IssueResponseTo>(updatedIssue);

			return responseDto;
		}

		public bool DeleteIssue(int id)
		{
			var issueToDelete = _issueRepository.GetById(id);
			if (issueToDelete == null)
			{
				return false;
			}

			_issueRepository.Delete(id);
			return true;
		}
	}
}
