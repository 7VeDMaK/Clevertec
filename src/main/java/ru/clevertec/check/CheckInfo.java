package main.java.ru.clevertec.check;

import java.util.Map;

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