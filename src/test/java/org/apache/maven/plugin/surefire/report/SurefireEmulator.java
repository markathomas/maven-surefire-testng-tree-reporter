package org.apache.maven.plugin.surefire.report;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.surefire.log.PluginConsoleLogger;
import org.apache.maven.surefire.api.report.RunMode;
import org.apache.maven.surefire.api.report.SimpleReportEntry;

import static java.util.stream.Collectors.toList;

public class SurefireEmulator {

    private final EmulatorLogger emulatorLogger = new EmulatorLogger();
    private final Utf8RecodingDeferredFileOutputStream stdout = new Utf8RecodingDeferredFileOutputStream("stdout");
    private final Utf8RecodingDeferredFileOutputStream stderr = new Utf8RecodingDeferredFileOutputStream("stderr");
    private final Class<?> clazz;
    private final ConsoleTreeReporter consoleTreeReporter;

    public SurefireEmulator(Class<?> clazz) {
        this(ReporterOptions.builder().build(), clazz);
    }

    public SurefireEmulator(ReporterOptions reporterOptions, Class<?> clazz) {
        this.clazz = clazz;
        this.consoleTreeReporter = new ConsoleTreeReporter(new PluginConsoleLogger(emulatorLogger), reporterOptions);
    }

    public List<String> run() {
        testsStarting();
        testsCompleted(testsSucceeded());
        return emulatorLogger.getLogList();
    }

    private void testsCompleted(TestSetStats testSetStats) {
        List<WrappedReportEntry> completedWrappedEntries =
                getAllInnerClasses(clazz).stream()
                        .map(this::simpleReportEntryGenerator)
                        .map(this::wrappedReportEntryGenerator)
                        .collect(toList());

        //List's head needs to be with complete testSetStats
        completedWrappedEntries.stream().findFirst()
                .ifPresent(i -> consoleTreeReporter.testSetCompleted(i, testSetStats, null));

        //List's tail goes with empty testSetStats
        completedWrappedEntries.stream().skip(1)
                .forEachOrdered(i -> consoleTreeReporter.testSetCompleted(i, new TestSetStats(false, false), null));
    }

    private TestSetStats testsSucceeded() {
        TestSetStats testSetStats = new TestSetStats(false, true);
        getAllMethods(getAllInnerClasses(clazz))
                .entrySet().stream()
                .flatMap((k) -> k.getValue().stream()
                        .map(i -> this.simpleReportEntryGenerator(k.getKey(), i))
                        .map(this::wrappedReportEntryGenerator))
                .forEachOrdered(testSetStats::testSucceeded);
        return testSetStats;
    }

    private void testsStarting() {
        getAllInnerClasses(clazz).stream()
                .map(this::simpleReportEntryGenerator)
                .forEachOrdered(consoleTreeReporter::testSetStarting);
    }

    private <T> SimpleReportEntry simpleReportEntryGenerator(Class<T> clazz) {
        return new SimpleReportEntry(RunMode.NORMAL_RUN, 123L, clazz.getName(), getClassDisplayName(clazz), null, null);
    }

    private <T> SimpleReportEntry simpleReportEntryGenerator(Class<T> clazz, Method method) {
        return new SimpleReportEntry(RunMode.NORMAL_RUN, 123L, clazz.getName(), getClassDisplayName(clazz), method.getName(), getMethodDisplayName(clazz, method));
    }

    private WrappedReportEntry wrappedReportEntryGenerator(SimpleReportEntry simpleReportEntry) {
        return new WrappedReportEntry(simpleReportEntry, ReportEntryType.SUCCESS, 1, stdout, stderr);
    }

    private List<Class<?>> getAllInnerClasses(Class<?> clazz) {
        return getAllInnerClasses(clazz, new ArrayList<>());
    }

    private List<Class<?>> getAllInnerClasses(Class<?> clazz, List<Class<?>> acc) {
        if (clazz.getDeclaredClasses().length == 0) {
            acc.add(clazz);
            return acc;
        }
        acc.add(clazz);
        acc.addAll(Arrays.stream(clazz.getDeclaredClasses())
                .flatMap(i -> getAllInnerClasses(i, new ArrayList<>()).stream())
                .collect(toList()));
        return acc;
    }

    private Map<Class<?>, List<Method>> getAllMethods(List<Class<?>> classes) {
        return classes.stream()
                .collect(Collectors.toMap(Function.identity(),
                        i -> Arrays.asList(i.getDeclaredMethods()),
                        (x, y) -> y, LinkedHashMap::new));
    }

    private <T> String getClassDisplayName(Class<T> clazz) {
        final String name = clazz.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private <T> String getMethodDisplayName(Class<T> clazz, Method method) {
        final String params = '(' + Stream.of(method.getParameterTypes()).map(Class::getSimpleName)
          .collect(Collectors.joining(", ")) + ')';
        return method.getName() + params;
    }
}
