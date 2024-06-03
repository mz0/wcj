package com.exactpro.build;

import com.exactpro.build.util.GitProjectSurveyor;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
class SemVerPlugin implements Plugin<Project> {
    private final static Logger logger = Logging.getLogger(SemVerPlugin.class);

    // see https://docs.gitlab.com/ee/ci/variables/predefined_variables.html
    private final static String GITLAB_MAIN_BRANCH = "CI_DEFAULT_BRANCH";
    // The branch or tag name for which project is built
    private final static String GITLAB_CI_BRANCH = "CI_COMMIT_REF_NAME";


    @Override
    public void apply(Project proj) {
        String fallbackTag = todoFallbackConfiguration(proj);
        var projectDir = proj.getLayout().getProjectDirectory().getAsFile().toPath();
        ProjectGitInfo surveyResult = null;
        try {
            var gitPath = locateGitDirectory(projectDir);
            surveyResult = new GitProjectSurveyor().survey(gitPath);
        } catch (IOException e) {
            logger.lifecycle("Git project '" + projectDir + "' survey error", e);
        }
        proj.setVersion(envSpecific(surveyResult).toString());
    }

    private String todoFallbackConfiguration(Project proj) {
        var extension = proj.getExtensions().create("semver", SemVerDslExtension.class);
        // TODO digest
        //  https://docs.gradle.org/current/userguide/implementing_gradle_plugins_binary.html#declaring_a_dsl_configuration_container
        //  https://docs.gradle.org/current/userguide/custom_gradle_types.html#read_only_managed_properties

        // Cannot query the value of extension 'semver' property 'fallbackVersion' because it has no value available.
        //System.out.println(extension.getFallbackVersion().get());
        return null;
    }

    private SemVer envSpecific(ProjectGitInfo gitInfo) {
        String suffix;
        if (System.getenv(GITLAB_CI_BRANCH) != null && System.getenv(GITLAB_MAIN_BRANCH) != null) {
            // Gitlab CI build environment
            suffix = System.getenv(GITLAB_CI_BRANCH).equals(System.getenv(GITLAB_MAIN_BRANCH)) ? null : "beta";
        } else {
            // local build environment; checking tree status
            // NB turns out JGit isClean() even when local tree is not in sync with remote tracking branch
            // unlike command-line `git` client
            suffix = gitInfo.getTreeStatus().isClean() ? "alpha" : "abnormal." + gitInfo.getCurrentBranch();
        }
        return gitInfo.getVersion().withSuffix(suffix);
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
