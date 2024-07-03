package main.java.ru.clevertec.check;

import main.java.ru.clevertec.check.Entity.Check;

import java.util.Arrays;
import java.util.List;

public class CheckRunner {
    static final String CSV_RESULT_FILE_NAME = "result.csv";
    private static final CheckConverter converter = new CheckConverter();
    private static final CheckFactory factory = new CheckFactory
            ("./src/main/resources/discountCards.csv",
                    "./src/main/resources/products.csv");
    private static final CSVWriter writer = new CSVWriter();

    public static void main(String[] args) {
        try {
            Check checkInfo = factory.createCheck(args);
            System.out.println(Arrays.toString(args));
            System.out.println(checkInfo);
            System.out.println("-----------------------------------------");
            List<String[]> data = converter.convertCheckInfoToCSV(checkInfo,
                    factory.getDiscountRepository(), factory.getProductRepository());
            data.forEach(line -> System.out.println(String.join(";", line)));
            writer.writeData(data, CSV_RESULT_FILE_NAME);
            System.out.println("-----------------------------------------");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            writer.writeError(CSV_RESULT_FILE_NAME, e.getMessage());
        }
    }
}//должен запускаться и реализовывать всё приложение. Он не должен делать работу сам, лишь проверять и выводить результаты