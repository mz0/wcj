package net.x320.build

import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging

import java.nio.file.Files
import java.nio.file.Path

@SuppressWarnings("unused")
class GitStatus implements Plugin<Project> {
    def logger = Logging.getLogger(GitStatus.class)

    @Override
    void apply(Project proj) {
        def projDir = proj.getLayout().getProjectDirectory().getAsFile().toPath()
        def currBranch = null
        try (Repository gitRepo = new FileRepository(projDir.resolve(".git").toFile())) {
            currBranch = gitRepo.getBranch()
        } catch (IOException e) {
            logger.lifecycle("Git Repository in '" + projDir + "' not found", e)
        }
        proj.getExtensions().add("currentBranch",
                currBranch != null ? currBranch : "unidentified Git branch")
    }
}
