namespace Publisher.Common
{
    public static class CacheKeys
    {
        public static string Entity(string name, object id) => $"{name.ToLower()}:{id}";
        public static string List(string name) => $"{name.ToLower()}:all";

        // Используем object, чтобы принимать и string, и long, и int
        public static string TopicsByUser(object userId) => $"topic:user:{userId}";
        public static string TopicsByTag(object tagId) => $"topic:tag:{tagId}";
        public static string ReactionsByTopic(object topicId) => $"reaction:topic:{topicId}";
        public static string TagsByTopic(object topicId) => $"tag:topic:{topicId}";
        public static string SearchResults() => "topic:search";
    }
}