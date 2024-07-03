package main.java.ru.clevertec.check;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRunner {
    static final String CSV_RESULT_FILE_NAME = "result.csv";
    static final String CSV_DISCOUNT_CARDS_FILE_NAME = "./src/main/resources/discountCards.csv";
    static final String CSV_PRODUCTS_FILE_NAME = "./src/main/resources/products.csv";
    private static final Map<Integer, Integer> discountCardMap = new HashMap<>();
    private static final Map<Integer, Product> productMap = new HashMap<>();
    private static final CheckDataToCSVConverter converter = new CheckDataToCSVConverter();
    private static final CSVWriter writer = new CSVWriter();


    static {
        try {
            loadDiscountCards();
            loadProducts();
        } catch (IOException e) {
            writer.writeError(CSV_RESULT_FILE_NAME, "INTERNAL SERVER ERROR");
            throw new CheckException("INTERNAL SERVER ERROR");
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

    public static Product getProductById(int id) {
        return productMap.get(id);
    }

    public static int getDiscount(Integer cardNumber) {
        if (cardNumber == null) return 0;
        return discountCardMap.getOrDefault(cardNumber, 2);
    }

    public static CheckInfo CreateCheckInfo(String[] args) {

        Map<String, Integer> productQuantities = new HashMap<>();
        String discountCard = null;
        String balanceDebitCard = null;

        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                String cardNumberStr = arg.split("=")[1];
                if (cardNumberStr.length() != 4 || !cardNumberStr.matches("\\d{4}")) {
                    throw new CheckException("BAD REQUEST");
                }
                discountCard = arg;
            } else if (arg.startsWith("balanceDebitCard=")) {
                balanceDebitCard = arg;
            } else if (arg.contains("-")) {
                String[] parts = arg.split("-", 2);
                if (parts.length == 2) {
                    int productId = Integer.parseInt(parts[0]);
                    try {
                        int quantity = Integer.parseInt(parts[1]);
                        Product product = getProductById(productId);
                        if (product == null || product.getQuantityInStock() < quantity) {
                            throw new CheckException("Product with ID " + productId + " not found. /" +
                                    " Not enough stock for product ID " + productId);
                        }
                        productQuantities.merge(parts[0], quantity, Integer::sum);
                    } catch (Exception e) {
                        throw new CheckException("BAD REQUEST");
                    }
                }
            }
        }
        if (productQuantities.isEmpty() || balanceDebitCard == null ||
                !balanceDebitCard.matches("balanceDebitCard=-?\\d+(\\.\\d{1,2})?")) {
            throw new CheckException("BAD REQUEST");
        } //At least one product must be selected.
        return new CheckInfo(productQuantities, discountCard, balanceDebitCard);

    }

    public static void main(String[] args) {
        try {
            CheckInfo checkInfo = CheckRunner.CreateCheckInfo(args);
            System.out.println(Arrays.toString(args));
            System.out.println(checkInfo);
            System.out.println("-----------------------------------------");
            List<String[]> data = converter.convertCheckInfoToCSV(checkInfo);
            data.forEach(line -> System.out.println(String.join(";", line)));
            writer.writeData(data, CSV_RESULT_FILE_NAME);
            System.out.println("-----------------------------------------");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            writer.writeError(CSV_RESULT_FILE_NAME, e.getMessage());
        }
    }
}