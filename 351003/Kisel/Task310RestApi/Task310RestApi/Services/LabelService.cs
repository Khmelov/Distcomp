
using AutoMapper;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Exceptions;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;

namespace Task310RestApi.Services
{
    public class LabelService : ILabelService
    {
        private readonly IRepository<Label> _labelRepository;
        private readonly IRepository<News> _newsRepository;
        private readonly IMapper _mapper;

        public LabelService(
            IRepository<Label> labelRepository,
            IRepository<News> newsRepository,
            IMapper mapper)
        {
            _labelRepository = labelRepository;
            _newsRepository = newsRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<LabelResponseTo>> GetAllLabelsAsync()
        {
            var labels = await _labelRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<LabelResponseTo>>(labels);
        }

        public async Task<LabelResponseTo?> GetLabelByIdAsync(long id)
        {
            var label = await _labelRepository.GetByIdAsync(id);
            if (label == null)
            {
                throw new ResourceNotFoundException($"Label not found with id: {id}");
            }
            return _mapper.Map<LabelResponseTo>(label);
        }

        public async Task<LabelResponseTo> CreateLabelAsync(LabelRequestTo labelRequest)
        {
            ValidateLabelRequest(labelRequest);
            
            var label = _mapper.Map<Label>(labelRequest);
            
            var createdLabel = await _labelRepository.CreateAsync(label);
            return _mapper.Map<LabelResponseTo>(createdLabel);
        }

        public async Task<LabelResponseTo?> UpdateLabelAsync(long id, LabelRequestTo labelRequest)
        {
            ValidateLabelRequest(labelRequest);
            
            var existingLabel = await _labelRepository.GetByIdAsync(id);
            if (existingLabel == null)
            {
                throw new ResourceNotFoundException($"Label not found with id: {id}");
            }

            existingLabel.Name = labelRequest.Name;
            
            var updatedLabel = await _labelRepository.UpdateAsync(existingLabel);
            return _mapper.Map<LabelResponseTo>(updatedLabel);
        }

        public async Task<bool> DeleteLabelAsync(long id)
        {
            if (!await _labelRepository.ExistsAsync(id))
            {
                throw new ResourceNotFoundException($"Label not found with id: {id}");
            }

            // Проверяем, используется ли метка в новостях
            var allNews = await _newsRepository.GetAllAsync();
            var newsUsingLabel = allNews.Any(n => n.LabelIds.Contains(id));
            if (newsUsingLabel)
            {
                throw new ValidationException("Cannot delete label that is used by news", "40004");
            }

            return await _labelRepository.DeleteAsync(id);
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await _labelRepository.ExistsAsync(id);
        }

        public async Task<IEnumerable<LabelResponseTo>> GetLabelsByNewsIdAsync(long newsId)
        {
            var news = await _newsRepository.GetByIdAsync(newsId);
            if (news == null)
            {
                throw new ResourceNotFoundException($"News not found with id: {newsId}");
            }

            var labels = new List<LabelResponseTo>();
            foreach (var labelId in news.LabelIds)
            {
                var label = await _labelRepository.GetByIdAsync(labelId);
                if (label != null)
                {
                    labels.Add(_mapper.Map<LabelResponseTo>(label));
                }
            }

            return labels;
        }

        private void ValidateLabelRequest(LabelRequestTo request)
        {
            var validationResults = new List<System.ComponentModel.DataAnnotations.ValidationResult>();
            var validationContext = new System.ComponentModel.DataAnnotations.ValidationContext(request);
            
            if (!System.ComponentModel.DataAnnotations.Validator.TryValidateObject(request, validationContext, validationResults, true))
            {
                var errorMessages = string.Join("; ", validationResults.Select(r => r.ErrorMessage));
                throw new ValidationException(errorMessages, "40000");
            }
        }
    }
}
