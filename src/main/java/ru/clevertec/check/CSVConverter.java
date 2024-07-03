package main.java.ru.clevertec.check;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVConverter {

    private String convertData(String[] data) {
        return String.join(";", data);
    }

    public boolean writeData(List<String[]> dataLines, String fileName) throws IOException {
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertData)
                    .forEach(pw::println);
        }
        return true;
    }

    public void writeError(String fileName, String errorMessage) {
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println("ERROR");
            pw.println(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//его задача конвертировать некую дату