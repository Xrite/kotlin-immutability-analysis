package org.jetbrains.research.ml.kotlinAnalysis.immutability

import org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies.Error

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

