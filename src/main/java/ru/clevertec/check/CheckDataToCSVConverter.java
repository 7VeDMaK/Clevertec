package main.java.ru.clevertec.check;

import main.java.ru.clevertec.check.Repository.CSVRepository;
import main.java.ru.clevertec.check.Repository.DiscountCSVRepository;
import main.java.ru.clevertec.check.Repository.ProductCSVRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckDataToCSVConverter {

    static final String CSV_DISCOUNT_CARDS_FILE_NAME = "./src/main/resources/discountCards.csv";
    static final String CSV_PRODUCTS_FILE_NAME = "./src/main/resources/products.csv";

    private CSVRepository<Integer, Integer> discountRepository = new DiscountCSVRepository();
    private CSVRepository<Integer, Product> productRepository = new ProductCSVRepository();

    {
        discountRepository.load(CSV_DISCOUNT_CARDS_FILE_NAME);
        productRepository.load(CSV_PRODUCTS_FILE_NAME);
    }

    public CheckInfo CreateCheckInfo(String[] args) {
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
                        Product product = productRepository.get(productId);
                        if (product == null || product.quantityInStock() < quantity) {
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
            product = productRepository.get(productId);
            int quantity = entry.getValue();
            double price = product.price();
            double itemTotal = price * quantity;
            double discountPercentage = (quantity >= 5 && product.wholesale()) ? 10.0 :
                    discountRepository.get(checkInfo.getDiscountCard());
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
                    discountRepository.get(checkInfo.getDiscountCard()) + "%"});
        }
        dataLines.add(new String[]{""});
        dataLines.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
        dataLines.add(new String[]{priceFormatter.format(total),
                priceFormatter.format(totalDiscount), priceFormatter.format(total - totalDiscount)});

        return dataLines;
    }
}
