
public interface IWriterService
{
    WriterResponseTo? GetById(long id);
    IEnumerable<WriterResponseTo> GetAll();
    WriterResponseTo Create(WriterRequestTo request);
    WriterResponseTo Update(WriterRequestTo request);
    void Delete(long id);
}