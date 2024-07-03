package main.java.ru.clevertec.check;

import main.java.ru.clevertec.check.Entity.Check;
import main.java.ru.clevertec.check.Entity.Product;
import main.java.ru.clevertec.check.Repository.DiscountCSVRepository;
import main.java.ru.clevertec.check.Repository.ProductCSVRepository;
import main.java.ru.clevertec.check.Validator.CheckFactoryValidator;

import java.util.HashMap;
import java.util.Map;

public class CheckFactory {

    private final DiscountCSVRepository discountRepository = new DiscountCSVRepository();
    private final ProductCSVRepository productRepository = new ProductCSVRepository();

    private final CheckFactoryValidator validator = new CheckFactoryValidator();

    CheckFactory(String discountCardsFileName, String productsFileName) {
        loadRepositories(discountCardsFileName, productsFileName);
    }

    CheckFactory() {
    }

    public DiscountCSVRepository getDiscountRepository() {
        return discountRepository;
    }

    public ProductCSVRepository getProductRepository() {
        return productRepository;
    }

    public void loadRepositories(String discountCardsFileName, String productsFileName) {
        discountRepository.load(discountCardsFileName);
        productRepository.load(productsFileName);
    }

    public Check createCheck(String[] args) {
        Map<String, Integer> productQuantities = new HashMap<>();
        String discountCard = null;
        String balanceDebitCard = null;

        for (String arg : args) {
            if (arg.startsWith("discountCard=")) {
                validator.validateDiscountCard(arg);
                discountCard = arg;
            } else if (arg.matches("balanceDebitCard=-?\\d+(\\.\\d{1,2})?")) {
                balanceDebitCard = arg;
            } else if (arg.contains("-")) {
                String[] parts = arg.split("-", 2);
                int productId = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);
                Product product = productRepository.get(productId);
                validator.validateProduct(product, quantity);
                productQuantities.merge(parts[0], quantity, Integer::sum);
            }
        }
        validator.validateAfter(productQuantities, balanceDebitCard, productRepository);

        return new Check(productQuantities, discountCard, balanceDebitCard);

    }
}
//Должен создавать информацию, передаваемую в конвертер