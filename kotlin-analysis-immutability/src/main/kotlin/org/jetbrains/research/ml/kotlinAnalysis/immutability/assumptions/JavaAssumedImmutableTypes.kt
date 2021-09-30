package org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions

import org.jetbrains.research.ml.kotlinAnalysis.immutability.Assumptions
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.immutable.ImmutableByAssumption

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
        ImmutabilityProperty.Immutable(ImmutableByAssumption)
    } else {
        null
    }
}
