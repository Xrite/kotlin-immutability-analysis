package org.jetbrains.research.ml.kotlinAnalysis.immutability.dependencies

import org.jetbrains.research.ml.kotlinAnalysis.immutability.Dependency
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityWithContext
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.Error

data class Error(
    val reason: Any?,
    val debug: List<Any?> = listOf()
) : Dependency() {
    override fun recalculate(immutability: ImmutabilityWithContext): ImmutabilityProperty =
        ImmutabilityProperty.Mutable(Error)
}
