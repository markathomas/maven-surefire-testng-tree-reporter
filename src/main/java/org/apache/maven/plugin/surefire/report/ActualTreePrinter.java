package org.apache.maven.plugin.surefire.report;

import org.apache.maven.surefire.shared.utils.logging.MessageBuilder;

import java.util.stream.LongStream;

import static org.apache.maven.surefire.shared.utils.logging.MessageUtils.buffer;

public class ActualTreePrinter {

    private final Node tree;

    public ActualTreePrinter(Node node) {
        this.tree = node;
    }

    public void print() {
        print(tree);
    }

    private void print(Node node) {
        printName(node);
        if (node.branches.isEmpty()) {
            node.wrappedReportEntries.forEach(i -> printTestFormated(node, i));
        }
        node.wrappedReportEntries.forEach(i -> printTestFormated(node, i));
        node.branches.forEach(this::print);
    }

    private void printTestFormated(Node node, WrappedReportEntry entry) {
        Theme theme = Theme.UNICODE;
        MessageBuilder builder = buffer();
        builder.a(theme.pipe());
        LongStream.rangeClosed(0, node.getDepth())
                .forEach(w -> builder.a(theme.blank()));
        builder.a(entry.getSourceName());
        System.out.println(builder);
    }

    private void printName(Node node) {
        Theme theme = Theme.UNICODE;
        MessageBuilder builder = buffer();
        builder.a(theme.pipe());
        LongStream.rangeClosed(0, node.getDepth() - 1)
                .forEach(w -> builder.a(theme.blank()));
        builder.a(node.getName());
        System.out.println(builder);
    }
}
