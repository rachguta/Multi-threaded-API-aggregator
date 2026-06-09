package org.example.filemanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.example.API;
import org.example.converter.CsvConverter;
import org.example.exception.FileProcessingException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CsvFileManager extends FileManager {

    public CsvFileManager(String dirName){
        super(dirName);
    }
    @Override
    public void writeToExistingFile(String fileName, JsonNode data) throws FileProcessingException {
        Path path = Path.of(dirName, fileName);

        if (!Files.exists(path)) {
            createNewFile(fileName);
        }

        if(data.isEmpty()){
            throw new FileProcessingException("Empty CSV rows, nothing to write to file " + path);
        }

        List<Map<String, String>> newRows = CsvConverter.convertToRows(data);

        List<Map<String, String>> allRows = new ArrayList<>();

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
            throw new FileProcessingException("Error reading file " + path);
        }

        allRows.addAll(newRows);
        writeToFile(path, allRows);
    }

    private void writeToFile(Path path, List<Map<String, String>> rows) throws FileProcessingException {
        if (rows.isEmpty()) {
            throw new FileProcessingException("Empty CSV rows, nothing to write to file " + path);
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
            throw new FileProcessingException("Error writing file " + path);
        }
    }

    @Override
    public void printAll(String fileName) throws FileProcessingException{
        Path path = Path.of(dirName, fileName);
        if (!Files.exists(path)) {
            throw new FileProcessingException("File not found " + path);
        }
        try(Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            lines.forEach(System.out::println);
        } catch (IOException e) {
            throw new FileProcessingException("Error reading file " + path);
        }
    }

    @Override
    public void printByApi(String fileName, API api) throws FileProcessingException {
        Path path = Path.of(dirName, fileName);
        boolean headerPrinted = false;
        if (!Files.exists(path)) {
            throw new FileProcessingException("File not found " + path);
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
                    if (!headerPrinted) {
                        printer.printRecord(parser.getHeaderNames());
                        headerPrinted = true;
                    }
                    printer.printRecord(record);
                    found = true;
                }
            }
            if (!found) {
                System.err.println("No records found for API " + api.name() + " in file " + path);
            }
        }catch (IOException e) {
            throw new FileProcessingException("Error reading file " + path);
        }
    }
}

