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
            logger.lifecycle("Git project '" + projDir + "' survey error", e)
        }
        proj.getExtensions().add("projectInfo", survey)
    }
}
/* TODO compare with Java plugin call-stack:
Git project '/home/mz0/p/web-crawl-java' survey error
java.io.IOException: /home/mz0/p/web-crawl-java/.kit is unlikely a Git repo
        at net.x320.build.util.GitProjectSurveyor.survey(GitProjectSurveyor.java:40)
        at net.x320.build.util.GitProjectSurveyor$survey.call(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:139)
        at net.x320.build.SemVerGit.apply(SemVerGit.groovy:22)
        at net.x320.build.SemVerGit.apply(SemVerGit.groovy)
        at org.gradle.api.internal.plugins.ImperativeOnlyPluginTarget.applyImperative(ImperativeOnlyPluginTarget.java:43)
        at org.gradle.api.internal.plugins.RuleBasedPluginTarget.applyImperative(RuleBasedPluginTarget.java:51)
        at org.gradle.api.internal.plugins.DefaultPluginManager.addPlugin(DefaultPluginManager.java:190)
        at org.gradle.api.internal.plugins.DefaultPluginManager.access$100(DefaultPluginManager.java:54)
        at org.gradle.api.internal.plugins.DefaultPluginManager$AddPluginBuildOperation.run(DefaultPluginManager.java:285)
        at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:29)
        at org.gradle.internal.operations.DefaultBuildOperationRunner$1.execute(DefaultBuildOperationRunner.java:26)
        at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:66)
        at org.gradle.internal.operations.DefaultBuildOperationRunner$2.execute(DefaultBuildOperationRunner.java:59)
        at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:157)
        at org.gradle.internal.operations.DefaultBuildOperationRunner.execute(DefaultBuildOperationRunner.java:59)
        at org.gradle.internal.operations.DefaultBuildOperationRunner.run(DefaultBuildOperationRunner.java:47)
        at org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:68)
        at org.gradle.api.internal.plugins.DefaultPluginManager.lambda$doApply$0(DefaultPluginManager.java:170)
        at org.gradle.internal.code.DefaultUserCodeApplicationContext.apply(DefaultUserCodeApplicationContext.java:43)
        at org.gradle.api.internal.plugins.DefaultPluginManager.doApply(DefaultPluginManager.java:169)
        at org.gradle.api.internal.plugins.DefaultPluginManager.apply(DefaultPluginManager.java:153)
        at org.gradle.api.internal.plugins.DefaultObjectConfigurationAction.applyType(DefaultObjectConfigurationAction.java:171)
        at org.gradle.api.internal.plugins.DefaultObjectConfigurationAction.applyPlugin(DefaultObjectConfigurationAction.java:155)
        at org.gradle.api.internal.plugins.DefaultObjectConfigurationAction.access$100(DefaultObjectConfigurationAction.java:43)
        at org.gradle.api.internal.plugins.DefaultObjectConfigurationAction$2.run(DefaultObjectConfigurationAction.java:87)
        at org.gradle.api.internal.plugins.DefaultObjectConfigurationAction.execute(DefaultObjectConfigurationAction.java:184)
        at org.gradle.api.internal.project.AbstractPluginAware.apply(AbstractPluginAware.java:49)
        at org.gradle.api.internal.project.ProjectScript.apply(ProjectScript.java:37)
        at org.gradle.api.Script$apply.callCurrent(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallCurrent(CallSiteArray.java:51)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:171)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:185)
        at build_8idgvol7klxv2zhxa7allyziq.run(/home/mz0/p/web-crawl-java/build.gradle:5)
 */
