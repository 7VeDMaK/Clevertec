package main.java.ru.clevertec.check.Entity;

import java.util.Map;

public class Check {
    private final Map<String, Integer> productQuantities;
    private Integer discountCard = null;
    private final Double balanceDebitCard;

    public Check(Map<String, Integer> productQuantities, String discountCard, String balanceDebitCard) {
        this.productQuantities = productQuantities;
        if (discountCard != null)
            this.discountCard = Integer.parseInt(discountCard.split("=")[1]);
        this.balanceDebitCard = Double.parseDouble(balanceDebitCard.split("=")[1]);
    }

    public Check(Map<String, Integer> productQuantities, Integer discountCard, Double balanceDebitCard) {
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