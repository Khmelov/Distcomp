using AutoMapper;
using Distcomp.Application.DTOs;
using Distcomp.Application.Exceptions;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;

namespace Distcomp.Application.Services
{
    public class IssueService : IIssueService
    {
        private readonly IRepository<Issue> _issueRepository;
        private readonly IRepository<User> _userRepository; 
        private readonly IMapper _mapper;

        public IssueService(IRepository<Issue> issueRepository, IRepository<User> userRepository, IMapper mapper)
        {
            _issueRepository = issueRepository;
            _userRepository = userRepository;
            _mapper = mapper;
        }

        public IssueResponseTo Create(IssueRequestTo request)
        {
            ValidateIssueRequest(request);

            if (_userRepository.GetById(request.UserId) == null)
                throw new RestException(404, 40401, $"User with id {request.UserId} not found. Cannot create issue.");

            var issue = _mapper.Map<Issue>(request);

            issue.Created = DateTime.UtcNow;
            issue.Modified = DateTime.UtcNow;

            var createdIssue = _issueRepository.Create(issue);
            return _mapper.Map<IssueResponseTo>(createdIssue);
        }

        public IssueResponseTo? GetById(long id)
        {
            var issue = _issueRepository.GetById(id);
            if (issue == null)
                throw new RestException(404, 40402, $"Issue with id {id} not found");

            return _mapper.Map<IssueResponseTo>(issue);
        }

        public IEnumerable<IssueResponseTo> GetAll()
        {
            return _mapper.Map<IEnumerable<IssueResponseTo>>(_issueRepository.GetAll());
        }

        public IssueResponseTo Update(long id, IssueRequestTo request)
        {
            var existingIssue = _issueRepository.GetById(id);
            if (existingIssue == null)
                throw new RestException(404, 40402, $"Cannot update: Issue with id {id} not found");

            ValidateIssueRequest(request);

            if (existingIssue.UserId != request.UserId && _userRepository.GetById(request.UserId) == null)
                throw new RestException(404, 40401, $"Cannot update issue: New User with id {request.UserId} not found");

            _mapper.Map(request, existingIssue);
            existingIssue.Id = id;
            existingIssue.Modified = DateTime.UtcNow;

            _issueRepository.Update(existingIssue);

            return _mapper.Map<IssueResponseTo>(existingIssue);
        }

        public bool Delete(long id)
        {
            var existingIssue = _issueRepository.GetById(id);
            if (existingIssue == null)
                throw new RestException(404, 40402, $"Cannot delete: Issue with id {id} not found");

            return _issueRepository.Delete(id);
        }

        private void ValidateIssueRequest(IssueRequestTo request)
        {
            if (request.Title.Length < 2 || request.Title.Length > 64)
                throw new RestException(400, 40005, "Title length must be between 2 and 64");

            if (request.Content.Length < 4 || request.Content.Length > 2048)
                throw new RestException(400, 40006, "Content length must be between 4 and 2048");
        }
    }
}