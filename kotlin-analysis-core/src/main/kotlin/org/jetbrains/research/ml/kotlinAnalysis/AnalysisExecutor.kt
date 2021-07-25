package org.jetbrains.research.ml.kotlinAnalysis

import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import org.jetbrains.idea.maven.project.MavenProjectsManager
import org.jetbrains.kotlin.analyzer.ModuleDescriptorListener
import org.jetbrains.kotlin.caches.resolve.KotlinCacheService
import org.jetbrains.kotlin.idea.caches.project.productionSourceInfo
import org.jetbrains.kotlin.idea.util.projectStructure.allModules
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.plugins.gradle.util.GradleConstants
import org.jetbrains.research.ml.kotlinAnalysis.util.getSubdirectories
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Abstract class for analysis executor which provides interface for execution analysis
 * for each project in given dataset.
 */
abstract class AnalysisExecutor {

    private val logger: Logger = Logger.getInstance(javaClass)

    /**
     * Set of resources which are under control of executor. Executor[AnalysisExecutor] runs their initialization
     * before analysis and close them after it ends.
     */
    abstract val controlledResourceManagers: Set<ResourceManager>

    /** Executes the analysis of the given [project][Project]. */
    abstract fun analyse(project: Project)

    /** Runs before analysis execution process. Contains all controlled resources initialization. */
    private fun init() {
        controlledResourceManagers.forEach { it.init() }
    }

    /** Runs after analysis execution process. Closes all controlled resource. */
    private fun close() {
        controlledResourceManagers.forEach { it.close() }
    }

    /** Execute analysis for all projects in [given directory][projectsDir]. */
    fun execute(
        projectsDir: Path,
        setupProject: (Path) -> Project = { projectPath -> ProjectUtil.openOrImport(projectPath, null, true) }
    ) {
        init()
        try {
            getSubdirectories(projectsDir).forEach { projectPath ->
                ApplicationManager.getApplication().invokeAndWait {
                    println("Opening project $projectPath")
                    setupProject(projectPath).let { project ->
                        try {
                            analyse(project)
                        } catch (ex: Exception) {
                            logger.error(ex)
                        } finally {
                            ApplicationManager.getApplication().invokeAndWait {
                                ProjectManagerEx.getInstanceEx().forceCloseProject(project)
                            }
                        }
                    }
                }
            }
        } finally {
            close()
        }
    }
}
