package org.example.filemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.API;
import org.example.exception.FileProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvFileManagerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private CsvFileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new CsvFileManager(tempDir.toString());
    }

    @Nested
    class WriteToExistingFileTests {

        @Test
        void shouldCreateNewCsvFileAndWriteData_whenFileDoesNotExist() {
            String fileName = "new_file.csv";
            ObjectNode record = mapper.createObjectNode()
                    .put("id", 1)
                    .put("source", "STEAM")
                    .put("timestamp", "2023-01-01")
                    .set("data", mapper.createObjectNode().put("game", "GTA"));

            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, record));

            Path createdFile = tempDir.resolve(fileName);
            assertTrue(Files.exists(createdFile));

            String content = assertDoesNotThrow(() -> Files.readString(createdFile));
            assertTrue(content.contains("id"));
            assertTrue(content.contains("source"));
            assertTrue(content.contains("STEAM.game"));
            assertTrue(content.contains("GTA"));
        }

        @Test
        void shouldAppendRowsToExistingCsvFile() {
            String fileName = "existing.csv";
            ObjectNode firstRecord = mapper.createObjectNode()
                    .put("id", 1)
                    .put("source", "STEAM")
                    .put("timestamp", "2023-01-01")
                    .set("data", mapper.createObjectNode().put("game", "GTA"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, firstRecord));

            ObjectNode secondRecord = mapper.createObjectNode()
                    .put("id", 2)
                    .put("source", "SPACEX")
                    .put("timestamp", "2023-01-02")
                    .set("data", mapper.createObjectNode().put("mission", "Falcon"));

            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, secondRecord));

            Path filePath = tempDir.resolve(fileName);
            List<CSVRecord> records = assertDoesNotThrow(() -> {
                try (Reader reader = Files.newBufferedReader(filePath)) {
                    CSVParser parser = CSVFormat.DEFAULT.builder()
                            .setHeader()
                            .setSkipHeaderRecord(true)
                            .get()
                            .parse(reader);
                    return parser.getRecords();
                }
            });

            assertEquals(2, records.size());
            assertEquals("1", records.get(0).get("id"));
            assertEquals("2", records.get(1).get("id"));
        }

        @Test
        void shouldThrowException_whenWritingEmptyObjectNode() {
            String fileName = "empty.csv";
            ObjectNode emptyData = mapper.createObjectNode();

            FileProcessingException exception = assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.writeToExistingFile(fileName, emptyData)
            );

            assertTrue(exception.getMessage().contains("Empty CSV rows"));
        }
    }

    @Nested
    class PrintAllTests {

        @Test
        void shouldPrintAllCsvLinesToSystemOut() {
            String fileName = "print_all.csv";
            ObjectNode firstRecord = mapper.createObjectNode()
                    .put("id", 1)
                    .put("source", "STEAM")
                    .put("timestamp", "2023-01-01")
                    .set("data", mapper.createObjectNode().put("game", "GTA"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, firstRecord));

            ObjectNode secondRecord = mapper.createObjectNode()
                    .put("id", 2)
                    .put("source", "SPACEX")
                    .put("timestamp", "2023-01-02")
                    .set("data", mapper.createObjectNode().put("mission", "Falcon"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, secondRecord));

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            assertDoesNotThrow(() -> fileManager.printAll(fileName));

            String output = outContent.toString();
            assertAll(
                    () -> assertTrue(output.contains("id")),
                    () -> assertTrue(output.contains("source")),
                    () -> assertTrue(output.contains("STEAM")),
                    () -> assertTrue(output.contains("SPACEX"))
            );

            System.setOut(System.out);
        }

        @Test
        void shouldThrowException_whenFileDoesNotExist() {
            String fileName = "nonexistent.csv";

            FileProcessingException exception = assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.printAll(fileName)
            );

            assertTrue(exception.getMessage().contains("File not found"));
        }
    }

    @Nested
    class PrintByApiTests {

        @Test
        void shouldPrintOnlyMatchingApiRecordsWithHeader() {
            String fileName = "mixed_api.csv";
            ObjectNode steamRecord = mapper.createObjectNode()
                    .put("id", 1)
                    .put("source", "STEAM")
                    .put("timestamp", "2023-01-01")
                    .set("data", mapper.createObjectNode().put("game", "GTA"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, steamRecord));

            ObjectNode spacexRecord = mapper.createObjectNode()
                    .put("id", 2)
                    .put("source", "SPACEX")
                    .put("timestamp", "2023-01-02")
                    .set("data", mapper.createObjectNode().put("mission", "Falcon"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, spacexRecord));

            ObjectNode steamRecord2 = mapper.createObjectNode()
                    .put("id", 3)
                    .put("source", "steam")
                    .put("timestamp", "2023-01-03")
                    .set("data", mapper.createObjectNode().put("game", "CS:GO"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, steamRecord2));

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            assertDoesNotThrow(() -> fileManager.printByApi(fileName, API.STEAM));

            String output = outContent.toString();
            assertAll(
                    () -> assertTrue(output.contains("GTA")),
                    () -> assertTrue(output.contains("CS:GO")),
                    () -> assertFalse(output.contains("Falcon"))
            );

            System.setOut(System.out);
        }

        @Test
        void shouldPrintToSystemErr_whenNoRecordsFound() {
            String fileName = "no_imdb.csv";
            ObjectNode steamRecord = mapper.createObjectNode()
                    .put("id", 1)
                    .put("source", "STEAM")
                    .put("timestamp", "2023-01-01")
                    .set("data", mapper.createObjectNode().put("game", "GTA"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, steamRecord));

            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent));

            assertDoesNotThrow(() -> fileManager.printByApi(fileName, API.IMDB));

            String errOutput = errContent.toString();
            assertTrue(errOutput.contains("No records found for API IMDB"));

            System.setErr(System.err);
        }

        @Test
        void shouldThrowException_whenFileDoesNotExist() {
            String fileName = "nonexistent.csv";

            FileProcessingException exception = assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.printByApi(fileName, API.STEAM)
            );

            assertTrue(exception.getMessage().contains("File not found"));
        }
    }
}