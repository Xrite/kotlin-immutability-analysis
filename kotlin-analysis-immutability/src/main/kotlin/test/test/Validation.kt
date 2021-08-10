package test.test

import com.intellij.openapi.project.Project

private fun countErrorDependencies(dependencies: Dependencies) =
    dependencies.count { it is Dependency.Error }

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
