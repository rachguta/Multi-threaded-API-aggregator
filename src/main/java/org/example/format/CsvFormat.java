package org.example.format;


import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.example.API;
import org.example.Converter;
import org.example.exceptions.FileProcessingException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvFormat extends FileFormat {

    @Override
    public void writeToNewFile(String fileName, ArrayNode data) {
        Path path = Path.of(dirName, fileName);
        List<Map<String, String>> rows = Converter.convertToRows(data);
        if (rows.isEmpty()) {
            System.err.println("Rows are empty, nothing to write to file " + path);
            return;
        }
        Set<String> headersSet = new LinkedHashSet<>();
        for (Map<String, String> row : rows) {
            headersSet.addAll(row.keySet());
        }
        String[] headers = headersSet.toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .get();
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(path.toString()), csvFormat)) {
            for (Map<String, String> row : rows) {
                List<String> values = new ArrayList<>(headers.length);
                for (String h : headers) {
                    values.add(row.getOrDefault(h, ""));
                }
                printer.printRecord(values);
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + path);
        }
    }

    @Override
    public void writeToExistingFile(String fileName, ArrayNode data) throws FileProcessingException {
        Path path = Path.of(dirName, fileName);
        List<Map<String, String>> newRows = Converter.convertToRows(data);
        List<Map<String, String>> allRows = new ArrayList<>();
        if(!Files.exists(path)) {
            throw new FileProcessingException("File " + path + " does not exist");
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get();
            CSVParser parser = format.parse(reader);
            for (CSVRecord record : parser) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : parser.getHeaderNames()) {
                    row.put(header, record.get(header));
                }
                allRows.add(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + path.toString());
            return;
        }

        allRows.addAll(newRows);

        if (allRows.isEmpty()) {
            System.err.println("Rows are empty, nothing to write to file " + path);
            return;
        }
        Set<String> headersSet = new LinkedHashSet<>();

        for (Map<String, String> row : allRows) {
            headersSet.addAll(row.keySet());
        }
        String[] headers = headersSet.toArray(new String[0]);
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .get();

        try (CSVPrinter printer = new CSVPrinter(new FileWriter(path.toString()), csvFormat)) {
            for (Map<String, String> row : allRows) {
                List<String> values = new ArrayList<>(headers.length);
                for (String h : headers) {
                    values.add(row.getOrDefault(h, ""));
                }
                printer.printRecord(values);
            }
        } catch (IOException e) {
            System.err.println("Error writing file " + path);
        }
    }

    @Override
    public void printAll(String fileName) {
        Path path = Path.of(dirName, fileName);
        if (!Files.exists(path)) {
            System.out.println(" File not found" + path);
            return;
        }
        try {
            Files.lines(path, StandardCharsets.UTF_8).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
        }
    }

    @Override
    public void printByApi(String fileName, API api) {
        Path path = Path.of(dirName, fileName);
        boolean headerPrinted = false;
        if (!Files.exists(path)) {
            System.out.println(" File not found: " + path);
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader);
             CSVPrinter printer = new CSVPrinter(System.out, CSVFormat.DEFAULT)) {

            boolean found = false;
            for (CSVRecord record : parser) {
                if (record.get("source").equalsIgnoreCase(api.name())) {
                    if(!headerPrinted){
                        printer.printRecord(parser.getHeaderNames());
                        headerPrinted = true;
                    }
                    printer.printRecord(record);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("️ There is no records for "+ api.name());
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + path);
        }
    }
}
