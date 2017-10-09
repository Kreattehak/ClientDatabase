package com.company.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.company.util.Mappings.ADDRESS_SERVICE_LOGGER_NAME;
import static com.company.util.Mappings.CLIENT_SERVICE_LOGGER_NAME;
import static com.company.util.Mappings.LOGIN_LOGGER_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
public class LoggerInjectorTest {

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private static final String INPUT = "Test message";
    private static final String DATABASE_LOGS =
            System.getProperty("user.home") + "\\ClientDatabase\\logs\\databaseLogs.log";
    private static final String DATABASE_ERRORS =
            System.getProperty("user.home") + "\\ClientDatabase\\logs\\databaseErrors.log";
    private static final String FULL_DATABASE_LOGS =
            System.getProperty("user.home") + "\\ClientDatabase\\logs\\fullDatabaseLogs.log";
    private static final String LOGIN_LOGS =
            System.getProperty("user.home") + "\\ClientDatabase\\logs\\loginLogs.log";

    private static Logger addressServiceLogger;
    private static Logger clientServiceLogger;
    private static Logger loginLogger;

    private static List<String> logFromDatabaseLogs;
    private static List<String> logFromDatabaseErrors;
    private static List<String> logFromFullDatabaseLogs;
    private static List<String> logFromLoginLogs;

    private static Pattern p = Pattern.compile("(?<=]).+(?=\\[)", Pattern.MULTILINE);

    private final String INFO = "INFO ";
    private final String TRACE = "TRACE";
    private final String ERROR = "ERROR";

    private final int databaseLogsSize = 5;
    private final int fullDatabaseLogsSize = 7;
    private final int databaseErrorsLogsSize = 2;
    private final int loginLogsSize = 1;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            Files.delete(Paths.get(DATABASE_LOGS));
            Files.delete(Paths.get(FULL_DATABASE_LOGS));
        } catch (NoSuchFileException e) {
            System.err.println("Files not found. Tests will proceed normally.");
        }

        System.setOut(new PrintStream(outContent));

        addressServiceLogger = LogManager.getLogger(ADDRESS_SERVICE_LOGGER_NAME);
        clientServiceLogger = LogManager.getLogger(CLIENT_SERVICE_LOGGER_NAME);
        loginLogger = LogManager.getLogger(LOGIN_LOGGER_NAME);

        addressServiceLogger.info(INPUT);
        addressServiceLogger.trace(INPUT);
        addressServiceLogger.error(INPUT);
        addressServiceLogger.warn(INPUT);
        clientServiceLogger.info(INPUT);
        clientServiceLogger.trace(INPUT);
        clientServiceLogger.error(INPUT);
        loginLogger.info(INPUT);

        logFromDatabaseLogs = Files.lines(Paths.get(DATABASE_LOGS))
                .collect(Collectors.toList());
        logFromDatabaseErrors = Files.lines(Paths.get(DATABASE_ERRORS))
                .collect(Collectors.toList());
        logFromFullDatabaseLogs = Files.lines(Paths.get(FULL_DATABASE_LOGS))
                .collect(Collectors.toList());
        logFromLoginLogs = Files.lines(Paths.get(LOGIN_LOGS))
                .collect(Collectors.toList());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        addressServiceLogger = null;
        clientServiceLogger = null;
        loginLogger = null;
    }

    @Test
    public void shouldAddLogsWithInfoLevelToDatabaseLogsThroughAddressAndClientServicesLogger() throws Exception {
        assertThat(cutOutTimeFromLogs(logFromDatabaseLogs), Matchers.<List<String>>allOf(
                hasSize(databaseLogsSize),
                hasItem(equalTo(prepareProperOutput(ADDRESS_SERVICE_LOGGER_NAME, INFO))),
                hasItem(equalTo(prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, INFO)))));
    }

    @Test
    public void shouldAddLogsWithTraceAndInfoLevelToDatabaseLogsThroughAddressAndClientServicesLogger() throws Exception {
        assertThat(cutOutTimeFromLogs(logFromFullDatabaseLogs), Matchers.<List<String>>allOf(
                hasSize(fullDatabaseLogsSize),
                hasItem(equalTo(prepareProperOutput(ADDRESS_SERVICE_LOGGER_NAME, INFO))),
                hasItem(equalTo(prepareProperOutput(ADDRESS_SERVICE_LOGGER_NAME, TRACE))),
                hasItem(equalTo(prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, INFO))),
                hasItem(equalTo(prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, TRACE)))));
    }

    @Test
    public void shouldAddLogsWithErrorLevelToDatabaseErrorsThroughAddressAndClientServicesLogger() throws Exception {
        assertThat(cutOutTimeFromLogs(logFromDatabaseErrors), Matchers.<List<String>>allOf(
                hasSize(databaseErrorsLogsSize),
                hasItem(equalTo(prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, ERROR))),
                hasItem(equalTo(prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, ERROR)))));
    }

    @Test
    public void shouldPrintErrorsFromAddressAndClientServiceLoggers() throws Exception {
        assertThat(replaceDateAndTimeWithEmptySpace(outContent.toString().trim()), equalTo(
                prepareProperOutput(ADDRESS_SERVICE_LOGGER_NAME, ERROR) + System.getProperty("line.separator")
                        + prepareProperOutput(CLIENT_SERVICE_LOGGER_NAME, ERROR)));
    }

    @Test
    public void shouldAddLogsWithInfoLevelToLoginLogsThroughLoginLogger() throws Exception {
        assertThat(cutOutTimeFromLogs(logFromLoginLogs), Matchers.<List<String>>allOf(
                hasSize(loginLogsSize),
                hasItem(equalTo(prepareProperOutput(LOGIN_LOGGER_NAME, INFO)))));
    }

    private static List<String> cutOutTimeFromLogs(List<String> logs) {
        return logs.stream()
                .map(LoggerInjectorTest::replaceDateAndTimeWithEmptySpace)
                .collect(Collectors.toList());
    }

    private static String replaceDateAndTimeWithEmptySpace(String log) {
        Matcher m = p.matcher(log);
        if (m.find()) {
            return m.replaceAll(" ");
        }
        return log;
    }

    private String prepareProperOutput(String loggerName, String level) {
        return String.format("%s [%s] [main] setUp-> %s", loggerName, level, INPUT);
    }

}