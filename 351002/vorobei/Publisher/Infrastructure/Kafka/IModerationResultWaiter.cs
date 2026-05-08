using BusinessLogic.DTO.Response;

namespace Infrastructure.Kafka
{
    public interface IModerationResultWaiter
    {
        void SetResult(int id, PostResponseTo result);
        void SetListResult(string key, List<PostResponseTo> result);
        Task<PostResponseTo?> WaitForResultAsync(int id, TimeSpan timeout);
        Task<List<PostResponseTo>?> WaitForListResultAsync(string key, TimeSpan timeout);
    }
}