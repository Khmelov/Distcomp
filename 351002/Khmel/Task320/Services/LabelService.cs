public class LabelService : ILabelService
{
    private readonly IRepository<Label> _repository;

    public LabelService(IRepository<Label> repository)
    {
        _repository = repository;
    }

    public LabelResponseTo GetById(long id)
    {
        var label = _repository.GetById(id);
        if (label == null)
            throw new KeyNotFoundException($"Label с id={id} не найден");
        return ToResponse(label);
    }

    public IEnumerable<LabelResponseTo> GetAll() =>
        _repository.GetAll().Select(ToResponse);

    public LabelResponseTo Create(LabelRequestTo request) =>
        ToResponse(_repository.Create(ToModel(request)));

    public LabelResponseTo Update(LabelRequestTo request) =>
        ToResponse(_repository.Update(ToModel(request)));

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Label с id={id} не найден");
    }

    private Label ToModel(LabelRequestTo dto) => new Label
    {
        Id = dto.Id,
        Name = dto.Name
    };

    private LabelResponseTo ToResponse(Label model) => new LabelResponseTo
    {
        Id = model.Id,
        Name = model.Name
    };
}