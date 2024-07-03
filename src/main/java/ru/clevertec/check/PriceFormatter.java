package main.java.ru.clevertec.check;

public class PriceFormatter {
    public String format(double price) {
        return String.format("%.2f$", price);
    }
}