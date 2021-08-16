package org.jetbrains.research.ml.kotlinAnalysis

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import org.jetbrains.idea.maven.project.MavenProjectsManager
import org.jetbrains.plugins.gradle.util.GradleConstants
import test.test.ImmutabilityAnalysisExecutor
import java.nio.file.Paths

object ImmutabilityArgs : RunnerArgs {

}

object ImmutabilityArgsParser : RunnerArgsParser<ImmutabilityArgs> {
    override fun parse(args: List<String>): ImmutabilityArgs {
        TODO("Not yet implemented")
    }
}

object KotlinImmutabilityAnalysisRunner : KotlinAnalysisRunner<IORunnerArgs, IORunnerArgsParser>
    ("kotlin-immutability-analysis", IORunnerArgsParser) {
    override fun run(args: IORunnerArgs) {
        ImmutabilityAnalysisExecutor(args.outputDir).execute(args.inputDir) { projectPath ->
            setUpProject(projectPath.toString())
        }
    }

    private fun setUpProject(projectPath: String): Project {
        val project: Project = ProjectUtil.openOrImport(Paths.get(projectPath))

        if (MavenProjectsManager.getInstance(project).isMavenizedProject) {
            MavenProjectsManager.getInstance(project).scheduleImportAndResolve()
            MavenProjectsManager.getInstance(project).importProjects()
        } else {
            ExternalSystemUtil.refreshProject(
                projectPath,
                ImportSpecBuilder(project, GradleConstants.SYSTEM_ID)
            )
        }

        return project
    }
}
