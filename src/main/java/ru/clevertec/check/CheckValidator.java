package main.java.ru.clevertec.check;

public class CheckValidator {
    public void validate(CheckInfo checkInfo) {
        if (checkInfo.getProductQuantities().isEmpty() || checkInfo.getBalanceDebitCard() == null) {
            throw new CheckException("BAD REQUEST");
        }
    }

    public void checkBalance(double balanceDebitCard, double total, double totalDiscount) {
        if (balanceDebitCard < total - totalDiscount) {
            throw new CheckException("NOT ENOUGH MONEY");
        }
    }
}