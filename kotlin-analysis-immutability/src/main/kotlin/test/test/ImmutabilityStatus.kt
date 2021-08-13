package test.test

import test.test.reasons.*

sealed class ImmutabilityStatus {
    data class Immutable(override val reasons: List<ImmutableReason> = listOf()) : ImmutabilityStatus() {
        constructor(vararg reasons: ImmutableReason) : this(reasons.toList())

        override fun isByAssumption(): Boolean = reasons.size == 1 && reasons[0].isByAssumption()

        override fun toString(): String {
            return "Immutable"
        }
    }

    data class ShallowImmutable(override val reasons: List<ShallowImmutableReason> = listOf()) : ImmutabilityStatus() {
        constructor(vararg reasons: ShallowImmutableReason) : this(reasons.toList())

        override fun isByAssumption(): Boolean = reasons.size == 1 && reasons[0].isByAssumption()

        override fun toString(): String {
            return "ShallowImmutable"
        }
    }

    data class ConditionallyDeeplyImmutable(
        val conditions: Set<Int>,
        override val reasons: List<ConditionallyDeeplyImmutableReason> = listOf()
    ) : ImmutabilityStatus() {
        constructor(conditions: Set<Int>, vararg reasons: ConditionallyDeeplyImmutableReason) : this(
            conditions,
            reasons.toList()
        )

        override fun isByAssumption(): Boolean = reasons.size == 1 && reasons[0].isByAssumption()

        override fun toString(): String {
            return "ConditionallyDeeplyImmutable($conditions)"
        }
    }

    data class Mutable(override val reasons: List<MutableReason> = listOf()) : ImmutabilityStatus() {
        constructor(vararg reasons: MutableReason) : this(reasons.toList())

        override fun isByAssumption(): Boolean = reasons.size == 1 && reasons[0].isByAssumption()

        override fun toString(): String {
            return "Mutable"
        }
    }

    abstract fun isByAssumption(): Boolean

    abstract val reasons: List<Reason>
}

fun join(statuses: List<ImmutabilityStatus>): ImmutabilityStatus = when {
    statuses.anyIsInstance<ImmutabilityStatus.Mutable>() -> {
        val reasons = statuses.filterIsInstance<ImmutabilityStatus.Mutable>().flatMap {
            it.reasons
        }
        ImmutabilityStatus.Mutable(reasons)
    }
    statuses.anyIsInstance<ImmutabilityStatus.ShallowImmutable>() -> {
        val reasons = statuses.filterIsInstance<ImmutabilityStatus.ShallowImmutable>().flatMap {
            it.reasons
        }
        ImmutabilityStatus.ShallowImmutable(reasons)
    }
    statuses.anyIsInstance<ImmutabilityStatus.ConditionallyDeeplyImmutable>() -> {
        val cdis = statuses.filterIsInstance<ImmutabilityStatus.ConditionallyDeeplyImmutable>()

        val conditions = cdis.flatMap { it.conditions }.toSet()
        val reasons = cdis.flatMap { it.reasons }
        ImmutabilityStatus.ConditionallyDeeplyImmutable(conditions, reasons)
    }
    else -> {
        val reasons = statuses.filterIsInstance<ImmutabilityStatus.Immutable>().flatMap {
            it.reasons
        }
        ImmutabilityStatus.Immutable(reasons)
    }
}

fun join(vararg statuses: ImmutabilityStatus): ImmutabilityStatus = join(statuses.toList())

infix fun ImmutabilityStatus.hasSameStatus(other: ImmutabilityStatus): Boolean {
    return this::class == other::class
}
