package org.apache.maven.plugin.surefire.report;

import org.apache.maven.surefire.shared.utils.logging.MessageBuilder;

import java.util.List;
import java.util.stream.LongStream;

import static org.apache.maven.plugin.surefire.report.TextFormatter.abbreviateName;
import static org.apache.maven.surefire.shared.utils.logging.MessageUtils.buffer;

public class ActualTreePrinter {
    private final Theme theme = Theme.UNICODE;
    private final Node tree;

    public ActualTreePrinter(Node node) {
        this.tree = node;
    }

    public void print() {
        print(tree.branches.get(0));
    }

    private void print(Node node) {
        printClass(node);
        node.wrappedReportEntries.forEach(i -> printTestFormated(node, i));
        node.branches.forEach(this::print);
    }

    private void printTestFormated(Node node, WrappedReportEntry testResult) {
//        if (testResult.isErrorOrFailure()) {
//            printFailure();
//        } else if (testResult.isSkipped()) {
//            printSkipped();
//        } else if (isPrintSuccessAllowed && testResult.isSucceeded()) {
        printSuccess(node, testResult);
//        }

    }

    private void printSuccess(Node node, WrappedReportEntry testResult) {
        printTestResult(buffer().success(theme.successful() + abbreviateName(testResult.getReportName())), node, testResult);
    }

    private void printTestResult(MessageBuilder builder, Node node, WrappedReportEntry testResult) {
        println(getTestPrefix(node, testResult)
                .a(builder)
                .a(" - " + testResult.elapsedTimeAsString())
                .toString());
    }

    private void println(String message) {
//        consoleLogger.info(message);
        System.out.println(message);
    }

    private boolean isLastMissingBranch(Node node) {
        Node rootChild = Node.getRoot().branches.get(0); // first after ROOT
        if (rootChild.hasBranches()) {
            Node rootChildLastChild = getLastItem(rootChild.branches); // last branch in root child
            return node.getParent(rootChildLastChild.getName()).isPresent() || node == rootChildLastChild;
        } else {
            return true;
        }
    }

    private <T> T getLastItem(List<T> list) {
        return list.get(list.size() - 1);
    }

    private MessageBuilder getTestPrefix(Node node, WrappedReportEntry testResult) {
        MessageBuilder builder = buffer();
        if (isLastMissingBranch(node))
            builder.a(theme.blank());
        else
            builder.a(theme.pipe());
        if (node.getDepth() > 1) {
            LongStream.rangeClosed(0, node.getDepth() - 3)
                    .forEach(i -> builder.a(theme.blank()));
            if (node.getParent().hasBranches() && node.hasBranches()
            ) {
                builder.a(theme.pipe());
            } else {
                builder.a(theme.blank());
            }
        }
        if (isLastTestToBeEval(node, testResult)) {
            builder.a(theme.entry());
        } else {
            builder.a(theme.end());
        }
        return builder;
    }

    private static boolean isLastTestToBeEval(Node node, WrappedReportEntry testResult) {
        return node.wrappedReportEntries.indexOf(testResult) + 1 != node.wrappedReportEntries.size();
    }

    private void printClass(Node node) {
        MessageBuilder builder = buffer();
        if (node.getDepth() > 1) {
            if (node.getDepth() > 2) {
                if (isLastMissingBranch(node)) builder.a(theme.blank());
                else builder.a(theme.pipe());
                LongStream.rangeClosed(0, node.getDepth() - 4)
                        .forEach(i -> builder.a(theme.blank()));
                builder.a(theme.end());
            } else {
                if (isLastMissingBranch(node)) builder.a(theme.end());
                else builder.a(theme.entry());
            }
            if (node.hasBranches()) {
                builder.a(theme.down());
            } else {
                builder.a(theme.dash());
            }
        } else {
            if (node.hasBranches()) builder.a(theme.down());
            else builder.a(theme.dash());
        }

        builder.a(node.getName());

//        concatenateWithTestGroup(builder, testResult, !isBlank(testResult.getReportNameWithGroup()));
//        builder.a(" - " + classResults.get(treeLength).elapsedTimeAsString());

        println(builder.toString());
    }
}
