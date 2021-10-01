package org.jetbrains.research.ml.kotlinAnalysis.immutability

import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.*
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.immutable.Initial

sealed class ImmutabilityProperty {
    data class Immutable(override val reasons: List<ImmutableReason> = listOf(Initial)) : ImmutabilityProperty() {
        constructor(vararg reasons: ImmutableReason) : this(reasons.toList())

        override fun toString(): String {
            return "Immutable"
        }
    }

    data class ShallowImmutable(override val reasons: List<ShallowImmutableReason> = listOf()) :
        ImmutabilityProperty() {
        constructor(vararg reasons: ShallowImmutableReason) : this(reasons.toList())

        override fun toString(): String {
            return "ShallowImmutable"
        }
    }

    data class ConditionallyDeeplyImmutable(
        val conditions: Set<Int>,
        override val reasons: List<ConditionallyDeeplyImmutableReason> = listOf()
    ) : ImmutabilityProperty() {
        constructor(conditions: Set<Int>, vararg reasons: ConditionallyDeeplyImmutableReason) : this(
            conditions,
            reasons.toList()
        )

        override fun toString(): String {
            return "ConditionallyDeeplyImmutable($conditions)"
        }
    }

    data class Mutable(override val reasons: List<MutableReason> = listOf()) : ImmutabilityProperty() {
        constructor(vararg reasons: MutableReason) : this(reasons.toList())

        override fun toString(): String {
            return "Mutable"
        }
    }

    fun isByAssumption(): Boolean = reasons.size == 1 && reasons[0].isByAssumption()

    abstract val reasons: List<Reason>
}

fun join(statuses: List<ImmutabilityProperty>): ImmutabilityProperty = when {
    statuses.anyIsInstance<ImmutabilityProperty.Mutable>() -> {
        val reasons = statuses.filterIsInstance<ImmutabilityProperty.Mutable>().flatMap {
            it.reasons
        }
        ImmutabilityProperty.Mutable(reasons)
    }
    statuses.anyIsInstance<ImmutabilityProperty.ShallowImmutable>() -> {
        val reasons = statuses.filterIsInstance<ImmutabilityProperty.ShallowImmutable>().flatMap {
            it.reasons
        }
        ImmutabilityProperty.ShallowImmutable(reasons)
    }
    statuses.anyIsInstance<ImmutabilityProperty.ConditionallyDeeplyImmutable>() -> {
        val cdis = statuses.filterIsInstance<ImmutabilityProperty.ConditionallyDeeplyImmutable>()

        val conditions = cdis.flatMap { it.conditions }.toSet()
        val reasons = cdis.flatMap { it.reasons }
        ImmutabilityProperty.ConditionallyDeeplyImmutable(conditions, reasons)
    }
    else -> {
        val reasons = statuses.filterIsInstance<ImmutabilityProperty.Immutable>().flatMap {
            it.reasons
        }
        ImmutabilityProperty.Immutable(reasons)
    }
}

fun join(vararg statuses: ImmutabilityProperty): ImmutabilityProperty = join(statuses.toList())


infix fun ImmutabilityProperty.hasSameStatus(other: ImmutabilityProperty): Boolean {
    return this == other
}
