namespace Publisher.Repository.Params {
    public class SearchParams : QueryParams {
        public string? Login { get; set; }
        public string? Firstname { get; set; }
        public string? Lastname { get; set; }
        public DateTime? FromDate { get; set; }
        public DateTime? ToDate { get; set; }
        public List<long>? Ids { get; set; }
        public string? Title { get; set; }
        public string? Content { get; set; }
        public long? EditorId { get; set; }
        public long? TweetId { get; set; }
    }
}