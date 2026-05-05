public interface ILabelService
{
    LabelResponseTo GetById(long id);
    IEnumerable<LabelResponseTo> GetAll();
    LabelResponseTo Create(LabelRequestTo request);
    LabelResponseTo Update(LabelRequestTo request);
    void Delete(long id);
}