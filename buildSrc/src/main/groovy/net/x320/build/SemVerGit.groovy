/*
 Immensely grateful to Andrew Oberstar <andrew@ajoberstar.org>
 for his 'reckon' Gradle plugin
 github.com/ajoberstar/reckon
 */
package net.x320.build

import net.x320.build.util.GitProjectSurveyor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging

@SuppressWarnings("unused")
class SemVerGit implements Plugin<Project> {
    def logger = Logging.getLogger(SemVerGit.class)

    @Override
    void apply(Project proj) {
        def projDir = proj.getLayout().getProjectDirectory().getAsFile().toPath()
        def survey = null
        try {
            survey = (new GitProjectSurveyor()).survey(projDir.resolve('.git'))
        } catch (IOException e) {
            logger.lifecycle("Git Repository '" + projDir + "' survey error", e)
        }
        proj.getExtensions().add("projectInfo", survey)
    }
}
