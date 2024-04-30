package net.x320.wcj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                logger.debug("Found HEAD {}", headObjectId.name());
                logger.info("`git status` is \"clean\": {}", isClean(repo));
            }
        } catch (IOException ex) {
            logger.error("Error reading .git/ in {}", currentDir, ex);
        }
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
                stat.getAdded(),
                stat.getChanged(),
                stat.getRemoved(),
                stat.getUntracked(),
                stat.getModified(),
                stat.getMissing()
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
