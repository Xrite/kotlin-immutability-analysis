package test.test

import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.ImmutableReason
import test.test.reasons.MutableReason

fun interface Assumptions {
    fun get(name: String): ImmutabilityProperty?
}

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
        "kotlin.Enum"
    )

    override fun get(name: String): ImmutabilityProperty? = if (name in types) {
        ImmutabilityProperty.Immutable()
    } else {
        null
    }
}

object KotlinCollections : Assumptions {
    val types = mapOf(
        "kotlin.collections.Iterable" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0), ConditionallyDeeplyImmutableReason.ByAssumption),
        "kotlin.collections.Collection" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0), ConditionallyDeeplyImmutableReason.ByAssumption),
        "kotlin.collections.List" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.Set" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.Map" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0, 1)),
        "kotlin.collections.MutableIterable" to ImmutabilityProperty.Mutable(MutableReason.ByAssumption),
        "kotlin.collections.MutableCollection" to ImmutabilityProperty.Mutable(MutableReason.ByAssumption),
        "kotlin.collections.MutableList" to ImmutabilityProperty.Mutable(MutableReason.ByAssumption),
        "kotlin.collections.MutableSet" to ImmutabilityProperty.Mutable(MutableReason.ByAssumption),
        "kotlin.collections.MutableMap" to ImmutabilityProperty.Mutable(MutableReason.ByAssumption)
    )

    override fun get(name: String): ImmutabilityProperty? = types[name]
}

object KotlinFunctions : Assumptions {
    override fun get(name: String): ImmutabilityProperty? =
        if ("kotlin.Function(0|[1-9][0-9]*)".toRegex().matches(name)) {
            ImmutabilityProperty.Immutable()
        } else {
            null
        }
}

object JavaAssumedImmutableTypes : Assumptions {
    val types = setOf(
        "java.io.File",
        "java.io.Serializable",
        "java.lang.CharSequence",
        "java.lang.Class",
        "java.lang.Cloneable",
        "java.lang.Comparable",
        "java.lang.Error",
        "java.lang.Exception",
        "java.lang.IllegalArgumentException",
        "java.lang.Iterable",
        "java.lang.Object",
        "java.lang.Process",
        "java.lang.Runnable",
        "java.lang.RuntimeException",
        "java.lang.String",
        "java.lang.Throwable",
        "java.math.BigInteger",
        "java.util.AbstractCollection",
        "java.util.AbstractSet",
        "java.util.Comparator",
    )

    override fun get(name: String): ImmutabilityProperty? = if (name in types) {
        ImmutabilityProperty.Immutable(ImmutableReason.ByAssumption)
    } else {
        null
    }
}
