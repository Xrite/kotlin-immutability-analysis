package test.test.assumptions

import test.test.Assumptions
import test.test.ImmutabilityProperty

object KotlinFunctions : Assumptions {
    override fun get(name: String): ImmutabilityProperty? =
        when {
            "kotlin.Function(0|[1-9][0-9]*)".toRegex().matches(name) -> ImmutabilityProperty.Immutable()
            name == "kotlin.jvm.functions.FunctionN" -> ImmutabilityProperty.Immutable()
            name == "kotlin.Function" -> ImmutabilityProperty.Immutable()
            else -> null
        }
}
