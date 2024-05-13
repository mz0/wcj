package net.x320.build;

import org.eclipse.jgit.api.Status;

public class GitProjectInfo {
    public static final String MAINLINE_PROPERTY = "MAINLINE";
    private final String currentBranch;
    private final String currentTag;
    private final String fullVersion;
    private final Status treeStatus;

    public GitProjectInfo(String currentBranch, String baseTag, String fullVersion, Status treeStatus) {
        this.currentBranch = currentBranch;
        this.currentTag = baseTag;
        this.fullVersion = fullVersion;
        this.treeStatus = treeStatus;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public String getBaseTag() {
        return currentTag;
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public Status getTreeStatus() {
        return treeStatus;
    }
}
