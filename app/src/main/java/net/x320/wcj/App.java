/*
 Immensely grateful to Andrew Oberstar <andrew@ajoberstar.org>
 for his 'reckon' Gradle plugin
 github.com/ajoberstar/reckon
 */
package net.x320.wcj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    private static final Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        var currentDir = Paths.get("").toAbsolutePath();
        try {
            var gitPath = locateGitDirectory(currentDir);
            logger.debug("Dir: {}; gitPath: {}", () -> currentDir, () -> gitPath);
            String currBranch;
            Repository repo;
            try (Repository gitRepo = new FileRepository(gitPath.toFile())) {
                repo = gitRepo;
                currBranch = gitRepo.getBranch();
            }
            logger.info("Dir: {}; currentBranch {}", currentDir, currBranch);
            try (var reader = repo.newObjectReader(); RevWalk walk = new RevWalk(reader)) {
                walk.setRetainBody(false); // we don't need commit bodies
                var headObjectId = repo.getRefDatabase().findRef("HEAD").getObjectId();
                if (headObjectId == null) {
                    logger.warn("No HEAD commit. Presuming repo is empty.");
                }
                logger.debug("Found HEAD {}", headObjectId == null ? "Error: not found" : headObjectId.name());
                logger.info("`git status` is \"clean\": {}", isClean(repo));
                final String semVer = "^v\\d{1,2}\\.\\d{1,4}\\.\\d{1,3}$";
                getTags(repo, walk, true, semVer);
                var headCommit = walk.parseCommit(headObjectId);
                var rcl = revList(walk, headCommit);
                logger.info("rcl.size: {}", rcl.size());
            }
        } catch (IOException ex) {
            logger.error("Error reading .git/ in {}", currentDir, ex);
        } catch (GitAPIException ex) {
            logger.error("Error reading Git status in {}", currentDir, ex);
        }
    }

    private static List<RevCommit> revList(RevWalk walk, RevCommit start) throws IOException {
        walk.reset();
        walk.setRevFilter(RevFilter.ALL);
        walk.markStart(start);
        ArrayList<RevCommit> commits = new ArrayList<>();
        for (RevCommit commit : walk) {
            logger.info("commit: {}", commit.toObjectId().name());
            commits.add(commit);
        }
        return commits;
    }

    private static Map<String, RevCommit> getTags(Repository repo, RevWalk walk, boolean onlyAnnotated, String match)
            throws IOException
    {
        var result = new HashMap<String, RevCommit>();
        for (var ref : repo.getRefDatabase().getRefsByPrefix(Constants.R_TAGS)) {
            var tag = repo.getRefDatabase().peel(ref);
            // only annotated tags return a peeled object id
            var isLightweight = tag.getPeeledObjectId() == null;
            var objectId =  !onlyAnnotated && isLightweight ? tag.getObjectId() : tag.getPeeledObjectId();
            if (objectId != null) {
                var commit = walk.parseCommit(objectId);
                var tagName = Repository.shortenRefName(ref.getName());
                if (tagName.matches(match)) {
                    result.put(tagName, commit);
                    logger.info("Found tag {} - commit {} annotated: {}",
                        tagName, commit.name(), !isLightweight);
                }

            }
        }
        return result;
    }

    private static boolean isClean(Repository repo) throws GitAPIException {
        var status = new Git(repo).status().call();
        printStatusIfDirty(status);
        return status.isClean();
    }

    private static void printStatusIfDirty(Status stat) {
        if (!stat.isClean() && logger.isTraceEnabled()) {
            logger.trace("Git status: added={}, changed={}, removed={},"
                        + " untracked={}, modified={}, missing={}",
                stat.getAdded(), stat.getChanged(), stat.getRemoved(),
                stat.getUntracked(), stat.getModified(), stat.getMissing()
            );
        }
    }

    private static Path locateGitDirectory(Path startPath) throws IOException {
        Path path = startPath;
        Path fsRoot = path.getRoot();
        Path gitPath = null;
        while (!fsRoot.equals(path)) {
            gitPath = path.resolve(".git");
            if (Files.exists(gitPath) && Files.isDirectory(gitPath)) {
                break;
            }
            gitPath = null;
            path = path.getParent();
        }
        if (gitPath == null) {
            throw new IOException(
                    String.format("Unable to locate git repository in %s", startPath)
            );
        }
        return gitPath;
    }
}
