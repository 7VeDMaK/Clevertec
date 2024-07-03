package main.java.ru.clevertec.check;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckDataToCSVConverter {

    private final PriceFormatter priceFormatter = new PriceFormatter();
    private final CheckValidator checkInfoValidator = new CheckValidator();

    public List<String[]> convertCheckInfoToCSV(CheckInfo checkInfo){
        checkInfoValidator.validate(checkInfo);

        double total = 0.0;
        double totalDiscount = 0.0;

        List<String[]> dataLines = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        dataLines.add(new String[]{"Date", "Time"});
        dataLines.add(new String[]{now.format(dateFormatter), now.format(timeFormatter)});
        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"QTY", "DESCRIPTION", "PRICE", "DISCOUNT", "TOTAL"});

        for (Map.Entry<String, Integer> entry : checkInfo.getProductQuantities().entrySet()) {
            int productId;
            Product product;
            productId = Integer.parseInt(entry.getKey());
            product = CheckRunner.getProductById(productId);
            int quantity = entry.getValue();
            double price = product.price();
            double itemTotal = price * quantity;
            double discountPercentage = (quantity >= 5 && product.wholesale()) ? 10.0 :
                    CheckRunner.getDiscount(checkInfo.getDiscountCard());
            double discount = (double) Math.round(itemTotal * discountPercentage) / 100;

            total += itemTotal;
            totalDiscount += discount;

            dataLines.add(new String[]{
                    String.valueOf(quantity),
                    product.description(),
                    priceFormatter.format(price),
                    priceFormatter.format(discount),
                    priceFormatter.format(itemTotal)
            });
        }

        checkInfoValidator.checkBalance(checkInfo.getBalanceDebitCard(), total, totalDiscount);

        if (checkInfo.getDiscountCard() != null) {
            dataLines.add(new String[]{""});
            dataLines.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
            dataLines.add(new String[]{String.valueOf(checkInfo.getDiscountCard()),
                    CheckRunner.getDiscount(checkInfo.getDiscountCard()) + "%"});
        }
        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
        dataLines.add(new String[]{priceFormatter.format(total),
                priceFormatter.format(totalDiscount), priceFormatter.format(total - totalDiscount)});

        return dataLines;
    }
}
