package by.bsuir.rest.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class MemoryStringCache implements StringCache {

    private final ConcurrentMap<String, String> values = new ConcurrentHashMap<>();

    @Override
    public String get(String key) {
        return values.get(key);
    }

    @Override
    public void put(String key, String value) {
        values.put(key, value);
    }

    @Override
    public void delete(String key) {
        values.remove(key);
    }
}
