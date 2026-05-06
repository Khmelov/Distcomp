public interface IWriterService
{
    WriterResponseTo GetById(long id);
    IEnumerable<WriterResponseTo> GetAll();
    WriterResponseTo Create(WriterRequestTo request);
    WriterResponseTo Update(WriterRequestTo request);
    void Delete(long id);
    Writer? GetByLogin(string login);
    bool VerifyPassword(string password, string hashedPassword);
}