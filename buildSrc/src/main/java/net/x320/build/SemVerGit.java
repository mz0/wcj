/*
 Immensely grateful to Andrew Oberstar <andrew@ajoberstar.org>
 for his 'reckon' Gradle plugin
 github.com/ajoberstar/reckon
 */
package net.x320.build;

import net.x320.build.util.GitProjectSurveyor;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.IOException;

@SuppressWarnings("unused")
class SemVerGit implements Plugin<Project> {
    private final static Logger logger = Logging.getLogger(SemVerGit.class);

    @Override
    public void apply(Project proj) {
        var projDir = proj.getLayout().getProjectDirectory().getAsFile().toPath();
        ProjectGitInfo survey = null;
        try {
            survey = (new GitProjectSurveyor()).survey(projDir.resolve(".git"));
        } catch (IOException e) {
            logger.lifecycle("Git project '" + projDir + "' survey error", e);
        }
        proj.getExtensions().add("projectInfo", survey);
        proj.setVersion(survey != null ? survey.getFullVersion() : "");
    }
}
