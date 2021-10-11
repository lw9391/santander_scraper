import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.Arrays;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class AcceptanceTestsRunner {

    private final SummaryGeneratingListener listener = new SummaryGeneratingListener();

    public static void main(String[] args) {
        AcceptanceTestsRunner runner = new AcceptanceTestsRunner();
        runner.runAcceptanceTests();
        TestExecutionSummary summary = runner.listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        summary.getFailures().forEach(runner::printFailureDetails);
    }

    private void runAcceptanceTests() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(AcceptanceTest.class))
                .build();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }

    private void printFailureDetails(TestExecutionSummary.Failure failure) {
        String testName = failure.getTestIdentifier().getDisplayName();
        String exceptionNAme = failure.getException().getClass().getName();
        String exceptionMessage = failure.getException().getMessage();
        System.out.println(testName);
        System.out.println(exceptionNAme);
        System.out.println(exceptionMessage);
        printFailureStackTrace(failure);
        System.out.println();
    }

    private void printFailureStackTrace(TestExecutionSummary.Failure failure) {
        StackTraceElement[] stackTrace = failure.getException().getStackTrace();
        Arrays.stream(stackTrace).forEach(System.out::println);
    }
}