namespace Project.Model {

    public class Editor : BaseEntity {
        public string Login { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
        public string Firstname { get; set; } = string.Empty;
        public string Lastname { get; set; } = string.Empty;

        public List<Tweet> Tweets { get; set; } = new();
    }

}