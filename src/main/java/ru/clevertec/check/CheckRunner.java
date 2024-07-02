package main.java.ru.clevertec.check;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckRunner {
    public class Product {
        private int id;
        private String description;
        private double price;
        private int quantityInStock;
        private boolean wholesale;

        public Product(int id, String description, double price, int quantityInStock, boolean wholesale) {
            this.id = id;
            this.description = description;
            this.price = price;
            this.quantityInStock = quantityInStock;
            this.wholesale = wholesale;
        }

        public int getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantityInStock() {
            return quantityInStock;
        }

        public boolean isWholesale() {
            return wholesale;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", description='" + description + '\'' +
                    ", price=" + price +
                    ", quantityInStock=" + quantityInStock +
                    ", wholesale=" + wholesale +
                    '}';
        }
    }
    public class CheckInfo {
        private Map<String, Integer> productQuantities;
        private Integer discountCard;
        private Double balanceDebitCard;

        public CheckInfo(Map<String, Integer> productQuantities, Integer discountCard, Double balanceDebitCard) {
            this.productQuantities = productQuantities;
            this.discountCard = discountCard;
            this.balanceDebitCard = balanceDebitCard;
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            productQuantities.forEach((id, quantity) -> output.append(id).append("-").append(quantity).append(" "));
            if (discountCard != null) {
                output.append(discountCard).append(" ");
            }
            if (balanceDebitCard != null) {
                output.append(balanceDebitCard);
            }
            return output.toString().trim();
        }

        public Map<String, Integer> getProductQuantities() {
            return productQuantities;
        }

        public Integer getDiscountCard() {
            return discountCard;
        }

        public Double getBalanceDebitCard() {
            return balanceDebitCard;
        }
    }

    static final String CSV_RESULT_FILE_NAME = "result.csv";
    static final String CSV_DISCOUNT_CARDS_FILE_NAME = "./src/main/resources/discountCards.csv";
    static final String CSV_PRODUCTS_FILE_NAME = "./src/main/resources/products.csv";

    private static final Map<Integer, Integer> discountCardMap = new HashMap<>();
    private static final Map<Integer, Product> productMap = new HashMap<>();

    static {
        try {
            loadDiscountCards();
        } catch (IOException e) {
            System.err.println("Error loading discount cards: " + e.getMessage());
        }

    }

    {
        try {
            loadProducts();
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
    }


    public String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public void convertCheckInfoToCSV(CheckInfo checkInfo) throws IOException {
        List<String[]> dataLines = new ArrayList<>();

        checkInfo.getProductQuantities().forEach((id, quantity) -> {
            dataLines.add(new String[]{id, String.valueOf(quantity)});
        });

        if (checkInfo.getDiscountCard() != null) {
            dataLines.add(new String[]{"discountCard", checkInfo.getDiscountCard().toString()});
        }
        if (checkInfo.getBalanceDebitCard() != null) {
            dataLines.add(new String[]{"balanceDebitCard", checkInfo.getBalanceDebitCard().toString()});
        }

        File csvOutputFile = new File(CSV_RESULT_FILE_NAME);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    public CheckInfo printAllInfo(String[] args) {
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

        return new CheckInfo(productQuantities, Integer.parseInt(discountCard.split("=")[1]),
                Double.parseDouble(balanceDebitCard.split("=")[1]));

    }

    private static void loadDiscountCards() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_DISCOUNT_CARDS_FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                if (values.length == 3) {
                    Integer number = Integer.parseInt(values[1]);
                    int discount = Integer.parseInt(values[2]);
                    discountCardMap.put(number, discount);
                }
            }
        }
    }

    private void loadProducts() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PRODUCTS_FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                if (values.length == 6) {
                    int id = Integer.parseInt(values[0]);
                    String description = values[1];
                    double price = Double.parseDouble(values[2]);
                    int quantityInStock = Integer.parseInt(values[3]);
                    boolean wholesale = values[4].equals("+");
                    productMap.put(id, new Product(id, description, price, quantityInStock, wholesale));
                }
            }
        }
        System.out.println(productMap.toString());
    }

    public static Product getProductById(int id) {
        return productMap.get(id);
    }

    public static int getDiscount(Integer cardNumber) {
        if (cardNumber == null || cardNumber.toString().length() != 4) {
            return 2;
        }
        return discountCardMap.getOrDefault(cardNumber, 2);
    }

    public static void main(String[] args) throws IOException {
        CheckRunner checkRunner = new CheckRunner();
        System.out.println(Arrays.toString(args));
        System.out.println(checkRunner.printAllInfo(args));
        checkRunner.convertCheckInfoToCSV(checkRunner.printAllInfo(args));

        int productId = 1; // For example
        Product product = getProductById(productId);
        if (product != null) {
            System.out.println(product);
        } else {
            System.out.println("Product with id " + productId + " not found.");
        }
    }
}
