package main.java.ru.clevertec.check.Repository;

import java.util.HashMap;
import java.util.Map;

public abstract class CSVRepository<K, V> {

    protected Map<K, V> repositoryMap = new HashMap<>();

    abstract public void load(String fileName);

    public V get(K key) {
        return repositoryMap.get(key);
    }

    public void put(K key, V value) {
        repositoryMap.put(key, value);
    }

    public boolean isEmpty(){
        return repositoryMap.isEmpty();
    }

}
