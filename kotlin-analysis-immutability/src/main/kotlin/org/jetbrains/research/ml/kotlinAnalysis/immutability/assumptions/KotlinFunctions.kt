package org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions

import org.jetbrains.research.ml.kotlinAnalysis.immutability.Assumptions
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty

object KotlinFunctions : Assumptions {
    override fun get(name: String): ImmutabilityProperty? =
        when {
            "kotlin.Function(0|[1-9][0-9]*)".toRegex().matches(name) -> ImmutabilityProperty.Immutable()
            name == "kotlin.jvm.functions.FunctionN" -> ImmutabilityProperty.Immutable()
            name == "kotlin.Function" -> ImmutabilityProperty.Immutable()
            else -> null
        }
}
