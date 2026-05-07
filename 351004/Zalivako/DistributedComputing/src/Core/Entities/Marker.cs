namespace Core.Entities
{
    public class Marker(string name) : Entity
    {
        public string Name { get; set; } = name;

        // navigation
        public List<News> News { get; set; } = [];
    }
}
