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
        private readonly IRepository<Marker> _markerRepository;
        private readonly IMapper _mapper;
        
        public IssueService(IRepository<Issue> issueRepository, IRepository<User> userRepository, IRepository<Marker> markerRepository, IMapper mapper)
        {
            _issueRepository = issueRepository;
            _userRepository = userRepository;
            _markerRepository = markerRepository;
            _mapper = mapper;
        }

        public IssueResponseTo Create(IssueRequestTo request)
        {
            ValidateIssueRequest(request);

            // 1. Проверка заголовка
            if (_issueRepository.GetAll().Any(i => i.Title == request.Title))
                throw new RestException(403, 40301, "Issue with this title already exists");

            // 2. Проверка пользователя
            if (_userRepository.GetById(request.UserId) == null)
                throw new RestException(404, 40401, "User not found");

            // 3. Маппинг (Markers проигнорируется благодаря настройке профиля выше)
            var issue = _mapper.Map<Issue>(request);
            issue.Markers = new List<Marker>();

            // 4. Логика "Найти или Создать" для маркеров по ИМЕНАМ (из теста)
            if (request.Markers != null && request.Markers.Any())
            {
                foreach (var markerName in request.Markers)
                {
                    // Ищем маркер. Важно: регистр должен совпадать с тем, что в тесте
                    var marker = _markerRepository.GetAll().FirstOrDefault(m => m.Name == markerName);

                    if (marker == null)
                    {
                        // Если маркера нет в tbl_marker — создаем его
                        marker = new Marker { Name = markerName };
                        _markerRepository.Create(marker);
                    }

                    issue.Markers.Add(marker);
                }
            }

            issue.Created = DateTime.UtcNow;
            issue.Modified = DateTime.UtcNow;

            // 5. Сохранение Issue (связи в tbl_issue_marker создадутся автоматически)
            var created = _issueRepository.Create(issue);
            return _mapper.Map<IssueResponseTo>(created);
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
            // Загружаем вместе с маркерами
            var existingIssue = _issueRepository.GetById(id);
            if (existingIssue == null)
                throw new RestException(404, 40402, "Issue not found");

            ValidateIssueRequest(request);

            // Маппим основные поля
            _mapper.Map(request, existingIssue);

            // Обновляем маркеры (очищаем и добавляем заново)
            existingIssue.Markers.Clear();
            if (request.Markers != null)
            {
                foreach (var name in request.Markers)
                {
                    var marker = _markerRepository.GetAll().FirstOrDefault(m => m.Name == name)
                                 ?? _markerRepository.Create(new Marker { Name = name });
                    existingIssue.Markers.Add(marker);
                }
            }

            existingIssue.Id = id;
            existingIssue.Modified = DateTime.UtcNow;
            _issueRepository.Update(existingIssue);

            return _mapper.Map<IssueResponseTo>(existingIssue);
        }

        public bool Delete(long id)
        {
            // 1. Находим тикет и его маркеры
            var issue = _issueRepository.GetById(id);
            if (issue == null)
                throw new RestException(404, 40402, $"Cannot delete: Issue with id {id} not found");

            // Запоминаем список маркеров этого тикета
            var issueMarkers = issue.Markers.ToList();

            // 2. Удаляем сам тикет
            var result = _issueRepository.Delete(id);

            // 3. Чистим маркеры, которые остались без тикетов
            if (result)
            {
                foreach (var marker in issueMarkers)
                {
                    // Проверяем, привязан ли этот маркер к какому-то ДРУГОМУ тикету
                    var isStillUsed = _issueRepository.GetAll()
                        .Any(i => i.Id != id && i.Markers.Any(m => m.Id == marker.Id));

                    if (!isStillUsed)
                    {
                        _markerRepository.Delete(marker.Id);
                    }
                }
            }

            return result;
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