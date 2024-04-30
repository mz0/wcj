package net.x320.wcj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

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
            try (Repository gitRepo = new FileRepository(gitPath.toFile())) {
                currBranch = gitRepo.getBranch();
            }
            logger.info("Dir: {}; currentBranch {}", currentDir, currBranch);
        } catch (IOException ex) {
            logger.error("Error reading .git/ in {}", currentDir, ex);
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
