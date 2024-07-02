package main.java.ru.clevertec.check;

import java.io.*;
import java.util.*;

public class CheckRunner {
    static final String CSV_RESULT_FILE_NAME = "result.csv";
    static final String CSV_DISCOUNT_CARDS_FILE_NAME = "./src/main/resources/discountCards.csv";
    static final String CSV_PRODUCTS_FILE_NAME = "./src/main/resources/products.csv";

    private static final Map<Integer, Integer> discountCardMap = new HashMap<>();
    private static final Map<Integer, Product> productMap = new HashMap<>();

    static {
        try {
            loadDiscountCards();
            loadProducts();
        } catch (IOException e) {
            System.err.println("Error loading discount cards or products: " + e.getMessage());
        }
    }

    private static void loadDiscountCards() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_DISCOUNT_CARDS_FILE_NAME))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    Integer number = Integer.parseInt(values[1]);
                    int discount = Integer.parseInt(values[2]);
                    discountCardMap.put(number, discount);
                }
            }
        }
    }

    private static void loadProducts() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PRODUCTS_FILE_NAME))) {
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
                    productMap.put(id, new Product(id, description, price, quantityInStock, wholesale));
                }
            }
        }
    }

    public CheckInfo CreateCheckInfo(String[] args) {
        Map<String, Integer> productQuantities = new HashMap<>();
        String discountCard = null;
        String balanceDebitCard = null;

        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                discountCard = arg;
            } else if (arg.startsWith("balanceDebitCard=")) {
                balanceDebitCard = arg;
            } else if (arg.contains("-")) {
                String[] parts = arg.split("-", 2);
                if (parts.length == 2) {
                    String productId = parts[0];
                    try {
                        int quantity = Integer.parseInt(parts[1]);
                        productQuantities.merge(productId, quantity, Integer::sum);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid quantity format: " + parts[1]);
                    }
                }
            }
        }

        return new CheckInfo(productQuantities, discountCard, balanceDebitCard);

    }

    public static Product getProductById(int id) {
        return productMap.get(id);
    }

    public static int getDiscount(Integer cardNumber) {
        if (cardNumber == null) return 0;
        return discountCardMap.getOrDefault(cardNumber, 2);
    } // Сделать исключения

    public static void main(String[] args) throws IOException {
        args = new String[]{"3-1", "2-5", "5-1", "3-1", "discountCard=1111", "balanceDebitCard=-100.01"};
        CheckRunner checkRunner = new CheckRunner();
        CheckDataToCSVConverter checkDataToCSVConverter = new CheckDataToCSVConverter();
        System.out.println(Arrays.toString(args));
        System.out.println(checkRunner.CreateCheckInfo(args));
        System.out.println();
        checkDataToCSVConverter.convertCheckInfoToCSV(checkRunner.CreateCheckInfo(args), CSV_RESULT_FILE_NAME);
    }
}//проверка на то, что если товара не хватает на скалде - ошибка