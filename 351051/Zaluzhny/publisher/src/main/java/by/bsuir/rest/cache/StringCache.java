package by.bsuir.rest.cache;

interface StringCache {

    String get(String key);

    void put(String key, String value);

    void delete(String key);
}
