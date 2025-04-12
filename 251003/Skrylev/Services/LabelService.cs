
public interface ILabelService
{
    Task<LabelResponseTo> CreateLabelAsync(LabelRequestTo labelDto);
    Task<LabelResponseTo> GetLabelByIdAsync(int id);
    Task<IEnumerable<LabelResponseTo>> GetAllLabelsAsync();
    Task<LabelResponseTo> UpdateLabelAsync(Label labelDto);
    Task<bool> DeleteLabelAsync(int id);
}
public class LabelService : ILabelService
{
    private readonly IGenericRepository<Label> _labelRepository;

    public LabelService(IGenericRepository<Label> labelRepository, ILogger<LabelService> logger)
    {
        _labelRepository = labelRepository;
    }

    public async Task<LabelResponseTo> CreateLabelAsync(LabelRequestTo labelDto)
    {
        var label = new Label
        {
            Name = labelDto.Name,
        };

        var createdLabel = await _labelRepository.AddAsync(label);
        return MapToResponse(createdLabel);
    }

    public async Task<bool> DeleteLabelAsync(int id)
    {
        var label = await _labelRepository.GetByIdAsync(id);
        if (label == null)
        {
            return false;
        }

        var deleted = await _labelRepository.DeleteAsync(id);
        return deleted;
    }

    public async Task<IEnumerable<LabelResponseTo>> GetAllLabelsAsync()
    {
        var labels = await _labelRepository.GetAllAsync();
        return labels.Select(MapToResponse);
    }

    public async Task<LabelResponseTo> GetLabelByIdAsync(int id)
    {
        var label = await _labelRepository.GetByIdAsync(id);
        if (label == null)
        {
            throw new KeyNotFoundException("Label not found.");
        }
        return MapToResponse(label);
    }

    public async Task<LabelResponseTo> UpdateLabelAsync(Label labelDto)
    {
        var label = await _labelRepository.GetByIdAsync(labelDto.id);
        if (label == null)
            throw new KeyNotFoundException("Label not found.");

        label.Name = labelDto.Name;

        var updatedLabel = await _labelRepository.UpdateAsync(label);
        return MapToResponse(updatedLabel);
    }

    private LabelResponseTo MapToResponse(Label label)
    {
        return new LabelResponseTo
        {
            Id = label.id,
            Name = label.Name,
        };
    }
}



