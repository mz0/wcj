package net.x320.build;

import org.eclipse.jgit.api.Status;

public class GitProjectInfo {
    private final String currentBranch;
    private final String currentTag;
    private final String fullVersion;
    private final Status treeStatus;

    public GitProjectInfo(String currentBranch, String currentTag, String fullVersion, Status treeStatus) {
        this.currentBranch = currentBranch;
        this.currentTag = currentTag;
        this.fullVersion = fullVersion;
        this.treeStatus = treeStatus;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public String getCurrentTag() {
        return currentTag;
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public Status getTreeStatus() {
        return treeStatus;
    }
}
