package org.example.parallel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.API;
import org.example.Aggregator;
import org.example.exception.FileProcessingException;
import org.example.filemanager.FileManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiPollingTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldCallSaveData_whenRequestAndConversionSuccessful() {
        Lock mockLock = mock(Lock.class);
        API api = API.STEAM;
        String fileName = "test.json";
        FileManager mockFileManager = mock(FileManager.class);
        Aggregator mockAggregator = mock(Aggregator.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        String fakeJson = "{\"field\": \"value\"}";
        JsonNode fakeNode = assertDoesNotThrow(() -> mapper.createObjectNode().put("id", 1));

        assertDoesNotThrow(() -> when(mockAggregator.sendRequest(api)).thenReturn(Optional.of(fakeJson)));
        when(mockAggregator.convertDataToJavaObject(fakeJson, api)).thenReturn(Optional.of(fakeNode));

        ApiPolling polling = new ApiPolling(mockLock, api, fileName, mockFileManager, mockAggregator);
        polling.setScheduledFuture(mockFuture);

        polling.run();

        verify(mockLock).lock();
        verify(mockLock).unlock();
        assertDoesNotThrow(() -> verify(mockAggregator).saveData(fileName, fakeNode, mockFileManager));
    }

    @Test
    void shouldThrowNoDataException_whenConversionReturnsEmpty() {
        Lock mockLock = mock(Lock.class);
        API api = API.STEAM;
        String fileName = "test.json";
        FileManager mockFileManager = mock(FileManager.class);
        Aggregator mockAggregator = mock(Aggregator.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        String fakeJson = "{\"field\": \"value\"}";

        assertDoesNotThrow(() ->when(mockAggregator.sendRequest(api)).thenReturn(Optional.of(fakeJson)));
        when(mockAggregator.convertDataToJavaObject(fakeJson, api)).thenReturn(Optional.empty());

        ApiPolling polling = new ApiPolling(mockLock, api, fileName, mockFileManager, mockAggregator);
        polling.setScheduledFuture(mockFuture);

        assertDoesNotThrow(polling::run);

        assertDoesNotThrow(() -> verify(mockAggregator, never()).saveData(anyString(), any(), any()));
    }

    @Test
    void shouldCancelFuture_whenIOExceptionOccurs() {
        Lock mockLock = mock(Lock.class);
        API api = API.STEAM;
        String fileName = "test.json";
        FileManager mockFileManager = mock(FileManager.class);
        Aggregator mockAggregator = mock(Aggregator.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        assertDoesNotThrow(() -> when(mockAggregator.sendRequest(api)).thenThrow(new IOException("Connection error")));

        ApiPolling polling = new ApiPolling(mockLock, api, fileName, mockFileManager, mockAggregator);
        polling.setScheduledFuture(mockFuture);

        assertDoesNotThrow(polling::run);

        verify(mockFuture).cancel(true);
        verify(mockLock, never()).lock();
    }

    @Test
    void shouldHandleFileProcessingException() {
        Lock mockLock = mock(Lock.class);
        API api = API.STEAM;
        String fileName = "test.json";
        FileManager mockFileManager = mock(FileManager.class);
        Aggregator mockAggregator = mock(Aggregator.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        String fakeJson = "{\"field\": \"value\"}";
        JsonNode fakeNode = assertDoesNotThrow(() -> mapper.createObjectNode().put("id", 1));

        assertDoesNotThrow(() -> when(mockAggregator.sendRequest(api)).thenReturn(Optional.of(fakeJson)));
        when(mockAggregator.convertDataToJavaObject(fakeJson, api)).thenReturn(Optional.of(fakeNode));
        assertDoesNotThrow(() -> doThrow(new FileProcessingException("Write error")).when(mockAggregator).saveData(fileName, fakeNode, mockFileManager));

        ApiPolling polling = new ApiPolling(mockLock, api, fileName, mockFileManager, mockAggregator);
        polling.setScheduledFuture(mockFuture);

        assertDoesNotThrow(polling::run);

        verify(mockLock).lock();
        verify(mockLock).unlock();
    }

    @Test
    void shouldNotLock_whenRequestReturnsEmpty() {
        Lock mockLock = mock(Lock.class);
        API api = API.STEAM;
        String fileName = "test.json";
        FileManager mockFileManager = mock(FileManager.class);
        Aggregator mockAggregator = mock(Aggregator.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        assertDoesNotThrow(() ->when(mockAggregator.sendRequest(api)).thenReturn(Optional.empty()));

        ApiPolling polling = new ApiPolling(mockLock, api, fileName, mockFileManager, mockAggregator);
        polling.setScheduledFuture(mockFuture);

        assertDoesNotThrow(polling::run);

        verify(mockLock, never()).lock();
        verify(mockLock, never()).unlock();
        verify(mockAggregator, never()).convertDataToJavaObject(anyString(), any());
    }
}