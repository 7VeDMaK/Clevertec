package main.java.ru.clevertec.check;

public class CheckRunner {
    public static void main(String[] args) {
        CheckExecutor executor = new CheckExecutor("./src/main/resources/discountCards.csv");
        executor.execute(args);
    }
}