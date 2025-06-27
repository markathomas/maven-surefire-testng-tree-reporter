package org.apache.maven.plugin.surefire.report;

import org.apache.maven.surefire.api.report.ReportEntry;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Node> branches = new ArrayList<>();
    private Node previous;
    private String name;
    private int nestLevel;

    Node(String name, int nestLevel) {
        this.name = name;
        this.nestLevel = nestLevel;
    }

    public Node addNode(ReportEntry reportEntry) {
        String[] nodes = reportEntry.getSourceName().split("\\$", -1);
        return addChild(nodes, 1);
    }

    private Node addChild(String[] nodes, int nestLevel) {
        if (nodes.length == nestLevel - 1) {
            return new Node(nodes[nestLevel - 2], nestLevel - 2);
        }
        Node node;
        if (contains(nodes[nestLevel -1]))
            node = branches.get(branches.indexOf(new Node(nodes[nestLevel - 1], nestLevel)));
        else {
            node = new Node(nodes[nestLevel - 1], nestLevel);
            this.branches.add(node);
        }
        return node.addChild(nodes, nestLevel + 1);
    }

    private boolean contains(String reportName) {
        return branches.stream().anyMatch((item) -> item.name.equals(reportName));
    }

    private String getSourceRootName(ReportEntry reportEntry) {
        return reportEntry.getSourceName().split("\\$", -1)[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node)
            return ((Node) obj).name.equals(this.name);
        else
            return false;
    }
}
