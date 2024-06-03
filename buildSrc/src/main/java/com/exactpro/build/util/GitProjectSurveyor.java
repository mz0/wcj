/*
 * Copyright (c) 2024 Exactpro Systems LLC
 * www.exactpro.com
 * Build Software to Test Software
 *
 * All rights reserved.
 * This is unpublished, licensed software, confidential and proprietary
 * information which is the property of Exactpro Systems LLC or its licensors.

 This code owes much to Andrew Oberstar <andrew@ajoberstar.org> and his
 'reckon' Gradle plugin (https://github.com/ajoberstar/reckon)
 */
package com.exactpro.build.util;

import com.exactpro.build.ProjectGitInfo;
import com.exactpro.build.SemVer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.exactpro.build.SemVer.TAG_PATTERN;

public class GitProjectSurveyor {
    public ProjectGitInfo survey(Path gitPath) throws IOException {
        try (Repository repo = new FileRepository(gitPath.toFile());
             var reader = repo.newObjectReader();
             var walk = new RevWalk(reader)
        ) {
            String currBranch = repo.getBranch();
            if (currBranch == null) {
                throw new IOException(String.format("%s is unlikely a Git repo", gitPath));
            }
            walk.setRetainBody(false); // we don't need commit bodies
            Ref headRef = repo.getRefDatabase().findRef("HEAD");
            if (headRef == null) {
                throw new IOException("No HEAD commit. Presuming repo is empty.");
            }
            Status treeStatus = getStatus(repo);
            var semVerOptional = maxVersion(getTaggedVersions(repo, walk, true));
            if (semVerOptional.isEmpty()) {
                throw new IllegalStateException(String.format("no semVer tags matching \"%s\" found ", TAG_PATTERN));
            }
            var latestTag = semVerOptional.get();
            var headCommit = walk.parseCommit(headRef.getObjectId());
            int patchIncrement = treeStatus.isClean() ? countCommits(walk, headCommit, latestTag.getCommit()) : 0;
            SemVer gitVersion = latestTag.getVersion().withPatchIncrementedBy(patchIncrement);
            return new ProjectGitInfo(currBranch, gitVersion, treeStatus);
        } catch (GitAPIException ex) {
            throw new IOException(String.format("Error reading Git status in %s", gitPath), ex);
        }
    }

    /** {@link org.eclipse.jgit.revwalk.RevWalkUtils#find} analog. */
    private static int countCommits(RevWalk walk, RevCommit start, RevCommit end) throws IOException {
        walk.reset();
        walk.markUninteresting(end);
        walk.markStart(start);
        int numCommits = 0;
        for (Iterator<RevCommit> it = walk.iterator(); it.hasNext(); it.next()) {
            numCommits += 1;
        }
        return numCommits;
    }

    private static List<VersionAndCommit> getTaggedVersions(
        Repository repo, RevWalk walk, boolean onlyAnnotated
    ) throws IOException {
        var versions = new ArrayList<VersionAndCommit>();
        for (Ref tagRef : repo.getRefDatabase().getRefsByPrefix(Constants.R_TAGS)) {
            Ref peeledTagRef = repo.getRefDatabase().peel(tagRef);
            ObjectId commitId = peeledTagRef.getPeeledObjectId() == null
                ? peeledTagRef.getObjectId()
                : peeledTagRef.getPeeledObjectId();
            if (commitId == null) {
                continue;
            }
            // Only annotated tags have a peeled object id.
            if (onlyAnnotated && (peeledTagRef.getPeeledObjectId() == null)) {
                continue;
            }
            String tagName = Repository.shortenRefName(tagRef.getName());
            if (!TAG_PATTERN.matcher(tagName).matches()) {
                continue;
            }
            RevCommit commit = walk.parseCommit(commitId);
            versions.add(new VersionAndCommit(SemVer.fromTagName(tagName), commit));
        }
        return versions;
    }

    private Optional<VersionAndCommit> maxVersion(List<VersionAndCommit> versions) {
        return versions.stream().max(Comparator.comparing(VersionAndCommit::getVersion));
    }

    private static Status getStatus(Repository repo) throws GitAPIException {
        return new Git(repo).status().call();
    }

    private static class VersionAndCommit {
        private final SemVer version;

        private final RevCommit commit;

        SemVer getVersion() {
            return version;
        }

        RevCommit getCommit() {
            return commit;
        }

        VersionAndCommit(SemVer version, RevCommit commit) {
            this.version = Objects.requireNonNull(version);
            this.commit = Objects.requireNonNull(commit);
        }
    }
}
