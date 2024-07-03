package main.java.ru.clevertec.check;

public class CheckRunner {
    public static void main(String[] args) {
        CheckExecutor executor = new CheckExecutor("result.csv",
                "./src/main/resources/discountCards.csv",
                "./src/main/resources/products.csv");
        executor.execute(args);
    }
}