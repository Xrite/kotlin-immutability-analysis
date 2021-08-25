package test.test.assumptions

import test.test.Assumptions
import test.test.ImmutabilityProperty

object KotlinBasicTypes : Assumptions {
    val types = setOf(
        "kotlin.Byte",
        "kotlin.Short",
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Float",
        "kotlin.Double",
        "kotlin.Boolean",
        "kotlin.Char",
        "kotlin.String",
        "kotlin.Any",
        "kotlin.Unit",
        "kotlin.Nothing",
        "kotlin.Enum",
        "kotlin.Annotation"
    )

    override fun get(name: String): ImmutabilityProperty? = if (name in types) {
        ImmutabilityProperty.Immutable()
    } else {
        null
    }
}
