using Publisher.Model;

namespace Publisher.Mapper {
    public class ColumnMapper {
        public Dictionary<Type, Dictionary<string, string>> _columnMappings = new();

        public ColumnMapper() {
            _columnMappings[typeof(Editor)] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase) {
                ["Id"] = "id",
                ["Login"] = "login",
                ["Password"] = "password",
                ["Firstname"] = "firstname",
                ["Lastname"] = "lastname"
            };

            _columnMappings[typeof(Tweet)] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase) {
                ["Id"] = "id",
                ["EditorId"] = "editor_id",
                ["Title"] = "title",
                ["Content"] = "content",
                ["Created"] = "created",
                ["Modified"] = "modified"
            };

            _columnMappings[typeof(Sticker)] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase) {
                ["Id"] = "id",
                ["Name"] = "name"
            };

            _columnMappings[typeof(Reaction)] = new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase) {
                ["Id"] = "id",
                ["TweetId"] = "tweet_id",
                ["Content"] = "content"
            };
        }
    }

}