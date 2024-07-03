package main.java.ru.clevertec.check.Repository;

import main.java.ru.clevertec.check.Entity.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductCSVRepository extends CSVRepository<Integer, Product> {
    @Override
    public void load(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Skip the header line
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {
                    int id = Integer.parseInt(values[0]);
                    String description = values[1];
                    double price = Double.parseDouble(values[2]);
                    int quantityInStock = Integer.parseInt(values[3]);
                    boolean wholesale = values[4].equals("+");
                    repositoryMap.put(id, new Product(id, description, price, quantityInStock, wholesale));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
