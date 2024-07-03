package main.java.ru.clevertec.check.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DiscountCSVRepository extends CSVRepository<Integer, Integer> {


    @Override
    public void load(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    Integer number = Integer.parseInt(values[1]);
                    int discount = Integer.parseInt(values[2]);
                    repositoryMap.put(number, discount);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer get(Integer key) {
        if (key == null) return 0;
        return repositoryMap.getOrDefault(key, 2);
    }
}
