package by.bsuir.rest.repository;

public record MarkerFilter(String name) {

    public static MarkerFilter empty() {
        return new MarkerFilter(null);
    }
}
