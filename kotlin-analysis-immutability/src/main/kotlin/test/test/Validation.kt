package test.test

import com.intellij.openapi.project.Project
import test.test.dependencies.Error

private fun countErrorDependencies(dependencies: Dependencies) =
    dependencies.count { it is Error }

fun validateEntities(entities: List<Entity>): Boolean {
    val failed = entities.count {
        when (it) {
            is ClassTemplate -> countErrorDependencies(it.dependencies) > 0
            ErrorTemplate -> true
        }
    }
    return failed <= entities.size / 2
}

fun validateProject(project: Project): Boolean {
    return true
}
