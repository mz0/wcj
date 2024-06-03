package com.exactpro.build;

import org.eclipse.jgit.api.Status;

public class ProjectGitInfo {
    private final String currentBranch;
    private final SemVer version;
    private final Status treeStatus;

    public ProjectGitInfo(String currentBranch, SemVer version, Status treeStatus) {
        this.currentBranch = currentBranch;
        this.version = version;
        this.treeStatus = treeStatus;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public SemVer getVersion() {
        return version;
    }

    public Status getTreeStatus() {
        return treeStatus;
    }
}
