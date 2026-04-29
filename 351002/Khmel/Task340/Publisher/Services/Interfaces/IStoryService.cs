public interface IStoryService
{
    StoryResponseTo GetById(long id);
    IEnumerable<StoryResponseTo> GetAll();
    StoryResponseTo Create(StoryRequestTo request);
    StoryResponseTo Update(StoryRequestTo request);
    void Delete(long id);
}