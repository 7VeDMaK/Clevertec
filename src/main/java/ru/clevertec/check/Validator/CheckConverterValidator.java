package main.java.ru.clevertec.check.Validator;

import main.java.ru.clevertec.check.Entity.Check;
import main.java.ru.clevertec.check.Exception.CheckException;

public class CheckConverterValidator {
    public void validate(Check checkInfo) {
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