package org.jetbrains.research.ml.kotlinAnalysis

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.xenomachina.argparser.ArgParser
import org.jetbrains.idea.maven.project.MavenProjectsManager
import org.jetbrains.plugins.gradle.util.GradleConstants
import test.test.Configuration
import test.test.ImmutabilityAnalysisExecutor
import java.nio.file.Paths


object KotlinImmutabilityAnalysisRunner : KotlinAnalysisRunner<IORunnerArgs, IORunnerArgsParser>
    ("kotlin-immutability-analysis", IORunnerArgsParser) {
    override fun run(args: IORunnerArgs) {
        val configurations = listOf(
            Configuration(
                treatCollectionsAsMutable = false,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = false,
                assumeNastyInheritors = false,
                assumeGoodGetters = false,
                includeTests = false,
                outputFileName = "results_default_without_tests.csv"
            ),
            Configuration(
                treatCollectionsAsMutable = false,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = false,
                assumeNastyInheritors = false,
                assumeGoodGetters = false,
                includeTests = true,
                outputFileName = "results_default_with_tests.csv"
            ),
            Configuration(
                treatCollectionsAsMutable = true,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = true,
                assumeNastyInheritors = true,
                assumeGoodGetters = false,
                includeTests = false,
                outputFileName = "results_pessimistic_without_tests.csv"
            ),
            Configuration(
                treatCollectionsAsMutable = true,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = true,
                assumeNastyInheritors = true,
                assumeGoodGetters = false,
                includeTests = true,
                outputFileName = "results_pessimistic_with_tests.csv"
            ),
            Configuration(
                treatCollectionsAsMutable = false,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = false,
                assumeNastyInheritors = false,
                assumeGoodGetters = true,
                includeTests = false,
                outputFileName = "results_optimistic_without_tests.csv"
            ),
            Configuration(
                treatCollectionsAsMutable = false,
                treatLazyAsImmutable = true,
                analyzeSealedSubclasses = false,
                assumeNastyInheritors = false,
                assumeGoodGetters = true,
                includeTests = true,
                outputFileName = "results_optimistic_with_tests.csv"
            ),
        )
        ImmutabilityAnalysisExecutor(args.outputDir, configurations).execute(args.inputDir) { projectPath ->
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
