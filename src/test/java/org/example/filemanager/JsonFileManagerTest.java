package org.example.filemanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.API;
import org.example.exception.FileProcessingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileManagerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private JsonFileManager fileManager;

    @BeforeEach
    void setUp() {
        fileManager = new JsonFileManager(tempDir.toString());
    }

    @Nested
    class WriteToExistingFileTests {

        @Test
        void shouldCreateNewFile_whenFileDoesNotExist() {
            String fileName = "new_file.json";
            ObjectNode newData = mapper.createObjectNode()
                    .put("name", "Test Item")
                    .put("value", 42);

            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, newData));

            Path createdFile = tempDir.resolve(fileName);
            assertTrue(Files.exists(createdFile));

            JsonNode result = assertDoesNotThrow(() -> mapper.readTree(createdFile.toFile()));
            assertTrue(result.isArray());
            assertEquals(1, result.size());
            assertEquals("Test Item", result.get(0).get("name").asText());
            assertEquals(42, result.get(0).get("value").asInt());
        }

        @Test
        void shouldAppendToExistingArray() {
            String fileName = "existing.json";
            Path filePath = tempDir.resolve(fileName);

            ArrayNode initialData = mapper.createArrayNode()
                    .add(mapper.createObjectNode().put("id", 1));
            assertDoesNotThrow(() -> mapper.writeValue(filePath.toFile(), initialData));

            ObjectNode newData = mapper.createObjectNode().put("id", 2);

            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, newData));

            JsonNode result = assertDoesNotThrow(() -> mapper.readTree(filePath.toFile()));
            assertEquals(2, result.size());
            assertEquals(1, result.get(0).get("id").asInt());
            assertEquals(2, result.get(1).get("id").asInt());
        }

        @Test
        void shouldWriteToEmptyFile() {
            String fileName = "empty.json";
            Path filePath = tempDir.resolve(fileName);
            assertDoesNotThrow(() -> Files.writeString(filePath, ""));

            ObjectNode newData = mapper.createObjectNode().put("test", "value");

            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, newData));

            JsonNode result = assertDoesNotThrow(() -> mapper.readTree(filePath.toFile()));
            assertTrue(result.isArray());
            assertEquals(1, result.size());
            assertEquals("value", result.get(0).get("test").asText());
        }

        @Test
        void shouldThrowException_whenFileIsNotArray() {
            String fileName = "not_array.json";
            Path filePath = tempDir.resolve(fileName);
            assertDoesNotThrow(() -> Files.writeString(filePath, "{\"error\": \"this is object\"}"));

            ObjectNode newData = mapper.createObjectNode().put("test", "value");

            FileProcessingException exception = assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.writeToExistingFile(fileName, newData)
            );

            assertTrue(exception.getMessage().contains("Error writing file"));
        }
    }

    @Nested
    class PrintAllTests {
        @Test
        void shouldPrintAllRecordsToSystemOut() {
            String fileName = "print_all.json";
            ArrayNode testData = mapper.createArrayNode()
                    .add(mapper.createObjectNode().put("source", "STEAM").put("game", "GTA"))
                    .add(mapper.createObjectNode().put("source", "SPACEX").put("mission", "Falcon"));
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, testData));

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            assertDoesNotThrow(() -> fileManager.printAll(fileName));

            String output = outContent.toString();
            assertAll(
                    () -> assertTrue(output.contains("STEAM")),
                    () -> assertTrue(output.contains("SPACEX")),
                    () -> assertTrue(output.contains("GTA")),
                    () -> assertTrue(output.contains("Falcon"))
            );

            System.setOut(System.out);
        }

        @Test
        void shouldThrowException_whenFileDoesNotExist() {
            String fileName = "nonexistent.json";
            assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.printAll(fileName)
            );
        }

        // java
        @Test
        void shouldHandleEmptyObject() {
            String fileName = "empty_object.json";
            ObjectNode emptyObject = mapper.createObjectNode();
            Path filePath = tempDir.resolve(fileName);

            assertDoesNotThrow(() -> mapper.writeValue(filePath.toFile(), emptyObject));

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));

            FileProcessingException exception = assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.printAll(fileName)
            );

            assertTrue(exception.getMessage().contains("Invalid JSON format in file"));

            System.setOut(originalOut);
        }

    }

    @Nested
    class PrintByApiTests {

        @Test
        void shouldPrintOnlyRecordsOfSpecifiedApi() {
            String fileName = "mixed_api.json";
            ObjectNode testData = mapper.createObjectNode().put("source", "STEAM").put("game", "GTA");
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, testData));

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            assertDoesNotThrow(() ->fileManager.printByApi(fileName, API.STEAM));

            String output = outContent.toString();
            assertTrue(output.contains("GTA"));

            System.setOut(System.out);
        }

        @Test
        void shouldPrintToSystemErr_whenNoRecordsFound() {
            String fileName = "no_imdb.json";
            ObjectNode testData = mapper.createObjectNode()
                    .put("source", "STEAM");
            assertDoesNotThrow(() -> fileManager.writeToExistingFile(fileName, testData));

            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errContent));

            assertDoesNotThrow(() -> fileManager.printByApi(fileName, API.IMDB));

            String errOutput = errContent.toString();
            assertTrue(errOutput.contains("No records found for API IMDB"));

            System.setErr(System.err);
        }

        @Test
        void shouldThrowException_whenFileDoesNotExist() {
            String fileName = "nonexistent.json";

            assertThrows(
                    FileProcessingException.class,
                    () -> fileManager.printByApi(fileName, API.STEAM)
            );
        }

    }

}