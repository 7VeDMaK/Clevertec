package main.java.ru.clevertec.check;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVWriter {

    public String convertData(String[] data) {
        return String.join(";", data);
    }

    public void writeData(List<String[]> dataLines, String fileName) throws IOException {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertData)
                    .forEach(pw::println);
        }
    }

    public void writeError(String fileName, String errorMessage) {
        if (fileName == null) fileName = "result.csv";
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("ERROR");
            pw.println(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}