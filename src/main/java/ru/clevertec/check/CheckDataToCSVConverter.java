package main.java.ru.clevertec.check;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataToCSVConverter {

    private String formatPrice(double price) {
        return String.format("%.2f$", price);
    }

    private String convertToCSV(String[] data) {
        return String.join(";", data);
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
            int productId;
            Product product;
            try {
                productId = Integer.parseInt(entry.getKey());
                product = CheckRunner.getProductById(productId);
            } catch (Exception e) {
                throw new CheckException("BAD REQUEST");
            }

            int quantity = entry.getValue();
            double price = product.getPrice();
            double itemTotal = price * quantity;

            double discountPercentage = (quantity >= 5 && product.isWholesale()) ? 10.0 : CheckRunner.getDiscount(checkInfo.getDiscountCard());
            double discount = (double) Math.round(itemTotal * discountPercentage) / 100;

            total += itemTotal;
            totalDiscount += discount;

            dataLines.add(new String[]{
                    String.valueOf(quantity),
                    product.getDescription(),
                    formatPrice(price),
                    formatPrice(discount),
                    formatPrice(itemTotal)
            });
        }
        if (checkInfo.getBalanceDebitCard() < total - totalDiscount) {
            throw new CheckException("NOT ENOUGH MONEY");
        }

        if (checkInfo.getDiscountCard() != null) {
            dataLines.add(new String[]{""});
            dataLines.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
            dataLines.add(new String[]{String.valueOf(checkInfo.getDiscountCard()), CheckRunner.getDiscount(checkInfo.getDiscountCard()) + "%"});
        }
        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
        dataLines.add(new String[]{formatPrice(total), formatPrice(totalDiscount), formatPrice(total - totalDiscount)});

        dataLines.forEach(line -> System.out.println(convertToCSV(line)));


    }
}
