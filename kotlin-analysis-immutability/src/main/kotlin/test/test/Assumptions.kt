package test.test

fun interface Assumptions {
    fun get(name: String): ImmutabilityStatus?
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

    override fun get(name: String): ImmutabilityStatus? = if (name in types) {
        ImmutabilityStatus.Immutable()
    } else {
        null
    }
}

object KotlinCollections : Assumptions {
    val types = mapOf(
        "kotlin.collections.Iterable" to ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.Collection" to ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.List" to ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.Set" to ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(0)),
        "kotlin.collections.Map" to ImmutabilityStatus.ConditionallyDeeplyImmutable(setOf(0, 1)),
        "kotlin.collections.MutableIterable" to ImmutabilityStatus.Mutable(),
        "kotlin.collections.MutableCollection" to ImmutabilityStatus.Mutable(),
        "kotlin.collections.MutableList" to ImmutabilityStatus.Mutable(),
        "kotlin.collections.MutableSet" to ImmutabilityStatus.Mutable(),
        "kotlin.collections.MutableMap" to ImmutabilityStatus.Mutable()
    )

    override fun get(name: String): ImmutabilityStatus? = types[name]
}

object KotlinFunctions : Assumptions {
    override fun get(name: String): ImmutabilityStatus? = if ("kotlin.Function(0|[1-9][0-9]*)".toRegex().matches(name)) {
        ImmutabilityStatus.Immutable()
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
        "java.util.Dictionary",
        "java.util.Enumeration",
        "java.util.Iterator",
        "java.util.List",
        "java.util.Map",
        "java.util.Random",
        "java.util.Set"
    )

    override fun get(name: String): ImmutabilityStatus? = if (name in types) {
        ImmutabilityStatus.Immutable()
    } else {
        null
    }
}
