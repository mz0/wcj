package com.exactpro.build;

import com.exactpro.util.Paths;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// former [Shsha] 4ed2395bf buildSrc/src/main/java/com/exactpro/build/GitStatus.java
public class GitStatus {

    public static String getBranch() {
        Path cwd = Paths.cwd();
        try {
            Path dotGitDir = locateGitDirectory(cwd);
            try (Repository gitRepo = new FileRepository(dotGitDir.toFile())) {
                return gitRepo.getBranch();
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                String.format("Error getting current Git branch: %s", ex.getMessage()),
                ex
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
            throw new IOException(String.format("Unable to locate Git repository for \"%s\"", startPath));
        }
        return gitPath;
    }
}
