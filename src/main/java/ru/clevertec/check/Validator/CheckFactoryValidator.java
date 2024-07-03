package main.java.ru.clevertec.check.Validator;

import main.java.ru.clevertec.check.Entity.Product;
import main.java.ru.clevertec.check.Exception.CheckException;
import main.java.ru.clevertec.check.Repository.ProductCSVRepository;

import java.util.Map;

public class CheckFactoryValidator {

    public void validateDiscountCard(String discountCard) {
        String cardNumberStr = discountCard.split("=")[1];
        if (cardNumberStr.length() != 4 || !cardNumberStr.matches("\\d{4}")) {
            throw new CheckException("BAD REQUEST");
        }
    }

    public void validateProduct(Product product, Integer quantity) {
        if (product == null || product.quantityInStock() < quantity) {
            throw new CheckException("BAD REQUEST");
        }
    }

    public void validateAfter(Map<String, Integer> productQuantities, String balanceDebitCard,
                              ProductCSVRepository productRepository) {
        if (productQuantities.isEmpty() || balanceDebitCard == null) {
            throw new CheckException("BAD REQUEST");
        } //At least one product must be selected.
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            int productId = Integer.parseInt(entry.getKey());
            int requestedQuantity = entry.getValue();
            Product product = productRepository.get(productId);
            if (product.quantityInStock() < requestedQuantity) {
                throw new CheckException("BAD REQUEST");
            }
        }
    }
}
