package test.test.assumptions

import test.test.Assumptions
import test.test.ImmutabilityProperty
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.MutableReason

class KotlinCollections(treatCollectionsAsMutable: Boolean) : Assumptions {
    val types = listOf(
        "kotlin.collections.MutableIterable",
        "kotlin.collections.MutableCollection",
        "kotlin.collections.MutableList",
        "kotlin.collections.MutableSet",
        "kotlin.collections.MutableMap"
    ).associateWith { ImmutabilityProperty.Mutable(MutableReason.ByAssumption) } +
            if (treatCollectionsAsMutable) {
                listOf(
                    "kotlin.collections.Iterable",
                    "kotlin.collections.Collection",
                    "kotlin.collections.List",
                    "kotlin.collections.Set",
                    "kotlin.collections.Map",
                ).associateWith { ImmutabilityProperty.Mutable(MutableReason.ByAssumption) }
            } else {
                mapOf(
                    "kotlin.collections.Iterable" to ImmutabilityProperty.ConditionallyDeeplyImmutable(
                        setOf(0),
                        ConditionallyDeeplyImmutableReason.ByAssumption
                    ),
                    "kotlin.collections.Collection" to ImmutabilityProperty.ConditionallyDeeplyImmutable(
                        setOf(0),
                        ConditionallyDeeplyImmutableReason.ByAssumption
                    ),
                    "kotlin.collections.List" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
                    "kotlin.collections.Set" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0)),
                    "kotlin.collections.Map" to ImmutabilityProperty.ConditionallyDeeplyImmutable(setOf(0, 1)),
                )
            }

    override fun get(name: String): ImmutabilityProperty? = types[name]
}
