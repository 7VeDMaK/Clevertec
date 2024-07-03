package main.java.ru.clevertec.check;

import main.java.ru.clevertec.check.Entity.Check;
import main.java.ru.clevertec.check.Exception.CheckException;

import java.util.List;

public class CheckExecutor {

    private String resultFileName;

    private String discountFileName;

    private String productFileName;
    private final CheckFactory factory;

    private final CSVWriter writer = new CSVWriter();
    private final CheckConverter converter = new CheckConverter();

    CheckExecutor(String resultFileName, String discountCardsFileName, String productsFileName) {
        factory = new CheckFactory(discountCardsFileName, productsFileName);
        this.resultFileName = resultFileName;
    }

    CheckExecutor(String discountCardsFileName) {
        factory = new CheckFactory();
        factory.loadDiscountRepository(discountCardsFileName);
        this.resultFileName = resultFileName;
    }

    CheckExecutor() {
        factory = new CheckFactory();
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public void setDiscountFileName(String discountFileName) {
        this.discountFileName = discountFileName;
    }

    public void setProductFileName(String productFileName) {
        this.productFileName = productFileName;
    }

    public boolean loadRepositoriesFromArgs(String[] args) {
        String pathToFile = null;
        String saveToFile = null;

        for (String arg : args) {
            if (arg.startsWith("pathToFile=")) {
                pathToFile = arg.split("=", 2)[1];
            } else if (arg.startsWith("saveToFile=")) {
                saveToFile = arg.split("=", 2)[1];
            }
        }

        if (pathToFile == null || saveToFile == null) {
            return false;
        }

        factory.loadProductRepository(pathToFile);
        resultFileName = saveToFile;
        return true;
    }

    public void execute(String[] args) {
        try {
            if (factory.areRepositoriesEmpty()) {
                if (!loadRepositoriesFromArgs(args))
                    throw new CheckException("BAD REQUEST");
            }

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
