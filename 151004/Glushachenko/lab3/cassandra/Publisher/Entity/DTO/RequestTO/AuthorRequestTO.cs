namespace Publisher.Entity.DTO.RequestTO
{
    public record class AuthorRequestTO(int Id, string Login, string Password, string FirstName, string LastName);
}
