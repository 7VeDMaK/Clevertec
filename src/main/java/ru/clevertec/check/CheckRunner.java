package main.java.ru.clevertec.check;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckRunner {
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

    final String CSV_FILE_NAME = "result.csv";

    private static final Map<Integer, Integer> discountCardMap = new HashMap<>();

    static {
        try {
            loadDiscountCards();
        } catch (IOException e) {
            System.err.println("Error loading discount cards: " + e.getMessage());
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

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(CheckInfo checkInfo) throws IOException {
        List<String[]> dataLines = new ArrayList<>();

        // Extract product quantities
        checkInfo.getProductQuantities().forEach((id, quantity) -> {
            dataLines.add(new String[]{id, String.valueOf(quantity)});
        });

        // Add discount card and balance debit card
        if (checkInfo.getDiscountCard() != null) {
            dataLines.add(new String[]{"discountCard", checkInfo.getDiscountCard().toString()});
        }
        if (checkInfo.getBalanceDebitCard() != null) {
            dataLines.add(new String[]{"balanceDebitCard", checkInfo.getBalanceDebitCard().toString()});
        }

        File csvOutputFile = new File(CSV_FILE_NAME);
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
        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/discountCards.csv"))) {
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

    public static int getDiscount(Integer cardNumber) {
        if (cardNumber == null || cardNumber.toString().length() != 4) {
            return 2;  // Default discount for invalid or non-4-digit numbers
        }
        return discountCardMap.getOrDefault(cardNumber, 2);
    }

    public static void main(String[] args) throws IOException {
        CheckRunner checkRunner = new CheckRunner();
        System.out.println(Arrays.toString(args));
        System.out.println(CheckRunner.getDiscount(checkRunner.printAllInfo(args).getDiscountCard()));
        checkRunner.givenDataArray_whenConvertToCSV_thenOutputCreated(checkRunner.printAllInfo(args));
    }
}
