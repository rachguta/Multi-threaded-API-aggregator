package org.example;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class CliManagerTest {

    @Nested
    class ReadApisTests {

        @Test
        void shouldReturnSingleApi_whenValidNameProvided() {
            Scanner scanner = new Scanner("steam\n");
            CliManager cli = new CliManager(scanner);
            API[] result = assertDoesNotThrow(cli::readApis);
            assertEquals(1, result.length);
            assertEquals(API.STEAM, result[0]);
        }

        @Test
        void shouldReturnMultipleApis_whenCommaSeparatedNamesProvided() {
            Scanner scanner = new Scanner("steam, spacex\n");
            CliManager cli = new CliManager(scanner);
            API[] result = assertDoesNotThrow(cli::readApis);
            assertEquals(2, result.length);
            assertEquals(API.STEAM, result[0]);
            assertEquals(API.SPACEX, result[1]);
        }

        @Test
        void shouldAskAgain_whenInvalidApiProvidedFirst() {
            Scanner scanner = new Scanner("invalid\nsteam\n");
            CliManager cli = new CliManager(scanner);
            API[] result = assertDoesNotThrow(cli::readApis);
            assertEquals(1, result.length);
            assertEquals(API.STEAM, result[0]);
        }
    }

    @Nested
    class RemoveSeparatorsTests {

        @Test
        void shouldSplitByCommaAndSemicolon() {
            String[] args = {"steam,spacex", "json", "5", "1.5"};
            var result = assertDoesNotThrow(() -> CliManager.removeSeparators(args));
            assertEquals(5, result.size());
            assertEquals("steam", result.get(0));
            assertEquals("spacex", result.get(1));
        }

        @Test
        void shouldThrowException_whenLessThanFourTokens() {
            String[] args = {"steam", "json"};
            assertThrows(IllegalArgumentException.class, () -> CliManager.removeSeparators(args));
        }
    }

    @Nested
    class ReadApisFromArgsTests {

        @Test
        void shouldReturnSingleApi_whenOneApiProvided() {
            var args = java.util.List.of("steam", "json", "5", "1.5");
            API[] result = assertDoesNotThrow(() -> CliManager.readApisFromArgs(args));
            assertEquals(1, result.length);
            assertEquals(API.STEAM, result[0]);
        }

        @Test
        void shouldThrowException_whenUnknownApi() {
            var args = java.util.List.of("unknown", "json", "5", "1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readApisFromArgs(args));
        }
    }

    @Nested
    class ReadFormatFromArgsTests {

        @Test
        void shouldReturnJson_whenFormatIsJson() {
            var args = java.util.List.of("steam", "json", "5", "1.5");
            String result = assertDoesNotThrow(() -> CliManager.readFormatFromArgs(args));
            assertEquals("json", result);
        }

        @Test
        void shouldThrowException_whenFormatIsUnknown() {
            var args = java.util.List.of("steam", "xml", "5", "1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readFormatFromArgs(args));
        }
    }

    @Nested
    class ReadMaxNumOfTasksFromArgsTests {

        @Test
        void shouldReturnPositiveInteger_whenValidNumberProvided() {
            List<String> args = List.of("steam", "json", "10", "1.5");
            int result = assertDoesNotThrow(() -> CliManager.readMaxNumOfTasksFromArgs(args));
            assertEquals(10, result);
        }

        @Test
        void shouldThrowException_whenNumberIsZero() {
            List<String> args = List.of("steam", "json", "0", "1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readMaxNumOfTasksFromArgs(args));
        }

        @Test
        void shouldThrowException_whenNumberIsNegative() {
            List<String> args = List.of("steam", "json", "-5", "1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readMaxNumOfTasksFromArgs(args));
        }

        @Test
        void shouldThrowException_whenNotANumber() {
            List<String> args = List.of("steam", "json", "abc", "1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readMaxNumOfTasksFromArgs(args));
        }
    }

    @Nested
    class ReadApiIntervalFromArgsTests {

        @Test
        void shouldReturnMilliseconds_whenValidPositiveDouble() {
            List<String> args = List.of("steam", "json", "5", "2.5");
            long result = assertDoesNotThrow(() -> CliManager.readApiIntervalFromArgs(args));
            assertEquals(2500, result);
        }

        @Test
        void shouldThrowException_whenIntervalIsZero() {
            List<String> args = List.of("steam", "json", "5", "0");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readApiIntervalFromArgs(args));
        }

        @Test
        void shouldThrowException_whenIntervalIsNegative() {
            List<String> args = List.of("steam", "json", "5", "-1.5");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readApiIntervalFromArgs(args));
        }

        @Test
        void shouldThrowException_whenNotANumber() {
            List<String> args = List.of("steam", "json", "5", "abc");
            assertThrows(IllegalArgumentException.class, () -> CliManager.readApiIntervalFromArgs(args));
        }
    }

    @Nested
    class ReadFormatTests {

        @Test
        void shouldReturnJson_whenUserEntersJson() {
            Scanner testScanner = new Scanner("json\n");
            CliManager cli = new CliManager(testScanner);

            String result = cli.readFormat();

            assertEquals("json", result);
        }

        @Test
        void shouldReturnCsv_whenUserEnters2() {
            Scanner testScanner = new Scanner("2\n");
            CliManager cli = new CliManager(testScanner);

            String result = cli.readFormat();

            assertEquals("csv", result);
        }

        @Test
        void shouldAskAgain_whenUserEntersInvalidValue() {
            Scanner testScanner = new Scanner("xml\njson\n");
            CliManager cli = new CliManager(testScanner);

            String result = cli.readFormat();

            assertEquals("json", result);
        }
    }

    @Nested
    class ReadParameterTests {

        @Test
        void shouldReturnUrlEncodedString_whenParameterContainsSpaces() {
            Scanner scanner = new Scanner("gta v\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(() -> cli.readParameter(API.STEAM));
            assertEquals("gta+v", result);
        }

        @Test
        void shouldReturnEmptyString_whenUserEntersEmptyLine() {
            Scanner scanner = new Scanner("\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(() -> cli.readParameter(API.STEAM));
            assertEquals("", result);
        }
    }

    @Nested
    class ReadFileNameTests {

        @Test
        void shouldReturnFileName_whenValid() {
            Scanner testScanner = new Scanner("my_file.json\n");
            CliManager cli = new CliManager(testScanner);

            String result = cli.readFileName();

            assertEquals("my_file.json", result);
        }

        @Test
        void shouldReplaceSpacesWithUnderscores() {
            Scanner testScanner = new Scanner("my file.json\n");
            CliManager cli = new CliManager(testScanner);

            String result = cli.readFileName();

            assertEquals("my_file.json", result);
        }
    }

    @Nested
    class ReadApiTests {

        @Test
        void shouldReturnApi_whenUserEntersNumber() {
            Scanner testScanner = new Scanner("1\n");
            CliManager cli = new CliManager(testScanner);

            API result = cli.readApi();

            assertEquals(API.STEAM, result);
        }

        @Test
        void shouldReturnApi_whenUserEntersName() {
            Scanner testScanner = new Scanner("spacex\n");
            CliManager cli = new CliManager(testScanner);

            API result = cli.readApi();

            assertEquals(API.SPACEX, result);
        }
    }

    @Nested
    class ReadFileModeTests {

        @Test
        void shouldReturnCreate_whenUserEnters1() {
            Scanner scanner = new Scanner("1\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(cli::readFileMode);
            assertEquals("create", result);
        }

        @Test
        void shouldReturnAdd_whenUserEntersAdd() {
            Scanner scanner = new Scanner("add\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(cli::readFileMode);
            assertEquals("add", result);
        }

        @Test
        void shouldAskAgain_whenUserEntersInvalidValue() {
            Scanner scanner = new Scanner("invalid\ncreate\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(cli::readFileMode);
            assertEquals("create", result);
        }
    }

    @Nested
    class ReadOutputModeTests {

        @Test
        void shouldReturnFully_whenUserEnters1() {
            Scanner scanner = new Scanner("1\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(cli::readOutputMode);
            assertEquals("fully", result);
        }

        @Test
        void shouldReturnByApi_whenUserEntersByApi() {
            Scanner scanner = new Scanner("by api\n");
            CliManager cli = new CliManager(scanner);
            String result = assertDoesNotThrow(cli::readOutputMode);
            assertEquals("by api", result);
        }
    }

    @Nested
    class ReadMaxNumOfTasksTests {

        @Test
        void shouldReturnNumber_whenValidPositiveInteger() {
            Scanner testScanner = new Scanner("10\n");
            CliManager cli = new CliManager(testScanner);

            int result = cli.readMaxNumOfTasks();

            assertEquals(10, result);
        }

        @Test
        void shouldAskAgain_whenUserEntersZero() {
            Scanner testScanner = new Scanner("0\n5\n");
            CliManager cli = new CliManager(testScanner);

            int result = cli.readMaxNumOfTasks();

            assertEquals(5, result);
        }
    }

    @Nested
    class ReadApiIntervalTests {

        @Test
        void shouldReturnMilliseconds_whenValidPositiveDouble() {
            Scanner testScanner = new Scanner("2.5\n");
            CliManager cli = new CliManager(testScanner);

            long result = cli.readApiInterval();

            assertEquals(2500, result);
        }

        @Test
        void shouldAskAgain_whenUserEntersNegative() {
            Scanner testScanner = new Scanner("-1.5\n3.0\n");
            CliManager cli = new CliManager(testScanner);

            long result = cli.readApiInterval();

            assertEquals(3000, result);
        }
    }

    @Nested
    class ReadStartingPollingTests {

        @Test
        void shouldNotThrow_whenUserPressesEnter() {
            Scanner scanner = new Scanner("\n");
            CliManager cli = new CliManager(scanner);
            assertDoesNotThrow(cli::readStartingPolling);
        }
    }

    @Nested
    class ReadStoppingPollingTests {

        @Test
        void shouldNotThrow_whenUserPressesEnter() {
            Scanner scanner = new Scanner("\n");
            CliManager cli = new CliManager(scanner);
            assertDoesNotThrow(cli::readStoppingPolling);
        }
    }

    @Nested
    class IsIntegerTests {

        @Test
        void shouldReturnTrue_whenStringIsValidPositiveInteger() {
            assertTrue(CliManager.isInteger("123"));
        }

        @Test
        void shouldReturnTrue_whenStringIsValidNegativeInteger() {
            assertTrue(CliManager.isInteger("-45"));
        }

        @Test
        void shouldReturnFalse_whenStringIsNotANumber() {
            assertFalse(CliManager.isInteger("abc"));
        }
    }




}