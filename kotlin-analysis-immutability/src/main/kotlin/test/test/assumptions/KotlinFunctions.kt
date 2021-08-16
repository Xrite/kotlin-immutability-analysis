package test.test.assumptions

import test.test.Assumptions
import test.test.ImmutabilityProperty

object KotlinFunctions : Assumptions {
    override fun get(name: String): ImmutabilityProperty? =
        if ("kotlin.Function(0|[1-9][0-9]*)".toRegex().matches(name)) {
            ImmutabilityProperty.Immutable()
        } else {
            null
        }
}
