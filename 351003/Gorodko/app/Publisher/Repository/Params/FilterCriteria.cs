namespace Publisher.Repository.Params {
    public enum FilterOperator {
        Equals,
        Contains,
        GreaterThan,
        LessThan,
        In
    }

    public class FilterCriteria<T> {
        public string? Field { get; set; }
        public string? Value { get; set; }
        public FilterOperator Operator { get; set; } = FilterOperator.Equals;
    }
}