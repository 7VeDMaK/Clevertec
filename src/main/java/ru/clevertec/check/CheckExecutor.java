package main.java.ru.clevertec.check;

import main.java.ru.clevertec.check.Entity.Check;

import java.util.List;

public class CheckExecutor {

    private String resultFileName;
    private final CheckFactory factory;

    private final CSVWriter writer = new CSVWriter();
    private final CheckConverter converter = new CheckConverter();

    CheckExecutor(String resultFileName, String discountCardsFileName, String productsFileName) {
        factory = new CheckFactory(discountCardsFileName, productsFileName);
        this.resultFileName = resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public void execute(String[] args) {
        try {
            Check checkInfo = factory.createCheck(args);
            System.out.println("-----------------------------------------");
            List<String[]> data = converter.convertCheckInfoToCSV(checkInfo,
                    factory.getDiscountRepository(), factory.getProductRepository());
            data.forEach(line -> System.out.println(String.join(";", line)));
            writer.writeData(data, resultFileName);
            System.out.println("-----------------------------------------");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            writer.writeError(resultFileName, e.getMessage());
        }
    }
}
