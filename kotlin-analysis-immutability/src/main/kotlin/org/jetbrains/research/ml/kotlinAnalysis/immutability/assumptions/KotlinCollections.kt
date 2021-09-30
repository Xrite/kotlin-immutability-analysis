package org.jetbrains.research.ml.kotlinAnalysis.immutability.assumptions

import org.jetbrains.research.ml.kotlinAnalysis.immutability.Assumptions
import org.jetbrains.research.ml.kotlinAnalysis.immutability.ImmutabilityProperty
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable.ConditionallyDeeplyImmutableByAssumption
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.mutable.MutableByAssumption

class KotlinCollections(treatCollectionsAsMutable: Boolean) : Assumptions {
    val types = listOf(
        "kotlin.collections.MutableIterable",
        "kotlin.collections.MutableCollection",
        "kotlin.collections.MutableList",
        "kotlin.collections.MutableSet",
        "kotlin.collections.MutableMap"
    ).associateWith { ImmutabilityProperty.Mutable(MutableByAssumption) } +
            if (treatCollectionsAsMutable) {
                listOf(
                    "kotlin.collections.Iterable",
                    "kotlin.collections.Collection",
                    "kotlin.collections.List",
                    "kotlin.collections.Set",
                    "kotlin.collections.Map",
                ).associateWith { ImmutabilityProperty.Mutable(MutableByAssumption) }
            } else {
                mapOf(
                    "kotlin.collections.Iterable" to ImmutabilityProperty.ConditionallyDeeplyImmutable(
                        setOf(0),
                        ConditionallyDeeplyImmutableByAssumption
                    ),
                    "kotlin.collections.Collection" to ImmutabilityProperty.ConditionallyDeeplyImmutable(
                        setOf(0),
                        ConditionallyDeeplyImmutableByAssumption
                    ),
                    "kotlin.collections.List" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
                    "kotlin.collections.Set" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
                    "kotlin.collections.Map" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0, 1)),
                )
            }

    override fun get(name: String): ImmutabilityProperty? = types[name]
}
