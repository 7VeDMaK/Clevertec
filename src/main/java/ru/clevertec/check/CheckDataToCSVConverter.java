package main.java.ru.clevertec.check;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckDataToCSVConverter {

    private String escapeSpecialCharacters(String data) {
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

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";"));
    }

    public void convertCheckInfoToCSV(CheckInfo checkInfo, String fileName) throws IOException {
        List<String[]> dataLines = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        dataLines.add(new String[]{"Date", "Time"});
        dataLines.add(new String[]{now.format(dateFormatter), now.format(timeFormatter)});
        dataLines.add(new String[]{""});

        double total = 0.0;
        double totalDiscount = 0.0;

        dataLines.add(new String[]{"QTY", "DESCRIPTION", "PRICE", "DISCOUNT", "TOTAL"});

        for (Map.Entry<String, Integer> entry : checkInfo.getProductQuantities().entrySet()) {
            int productId = Integer.parseInt(entry.getKey());
            Product product = CheckRunner.getProductById(productId);
            int quantity = entry.getValue();
            double price = product.getPrice();
            double itemTotal = price * quantity;
            double discountPercentage = CheckRunner.getDiscount(checkInfo.getDiscountCard());
            double discount = itemTotal * discountPercentage / 100.0;

            total += itemTotal;
            totalDiscount += discount;

            dataLines.add(new String[]{
                    String.valueOf(quantity),
                    product.getDescription(),
                    String.format("%.2f$", price),
                    String.format("%.2f$", discount),
                    String.format("%.2f$", itemTotal)
            });
        }

        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
        dataLines.add(new String[]{String.valueOf(checkInfo.getDiscountCard()), CheckRunner.getDiscount(checkInfo.getDiscountCard()) + "%"});
        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
        dataLines.add(new String[]{String.format("%.2f$", total), String.format("%.2f$", totalDiscount), String.format("%.2f$", total - totalDiscount)});

        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }
}
