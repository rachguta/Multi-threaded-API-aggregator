package org.example.filemanager;

import org.example.exception.FileProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
class FileManagerTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldCreateNewEmptyFile_whenFileDoesNotExist() throws Exception {
        FileManager fileManager = new JsonFileManager(tempDir.toString());
        String fileName = "brand_new_file.json";
        Path expectedPath = tempDir.resolve(fileName);
        assertFalse(Files.exists(expectedPath));

        fileManager.createNewFile(fileName);

        assertTrue(Files.exists(expectedPath));
        assertEquals(0, Files.size(expectedPath));
    }
    @Test
    void shouldTruncateExistingFile_whenFileAlreadyExists() {
        FileManager fileManager = new JsonFileManager(tempDir.toString());
        String fileName = "existing_file.json";
        Path targetPath = tempDir.resolve(fileName);

        assertDoesNotThrow(() -> Files.writeString(targetPath, "Old data that should be deleted"));
        assertTrue(assertDoesNotThrow(() -> Files.size(targetPath) > 0));

        assertDoesNotThrow(() -> fileManager.createNewFile(fileName));

        assertEquals(0, assertDoesNotThrow(() -> Files.size(targetPath)));
    }

    @Test
    void shouldThrowFileProcessingException_whenIoExceptionOccurs() {
        FileManager fileManager = new JsonFileManager(tempDir.toString());
        String invalidFileName = "invalid<>file:name?.json";

        FileProcessingException exception = assertThrows(
                FileProcessingException.class,
                () -> fileManager.createNewFile(invalidFileName)
        );

        assertTrue(exception.getMessage().contains("Invalid file name"));
    }


}