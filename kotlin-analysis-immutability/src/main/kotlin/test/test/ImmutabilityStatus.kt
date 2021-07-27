package test.test

sealed class ImmutabilityStatus {
    data class Immutable(val reasons: List<ImmutableReason> = listOf()) : ImmutabilityStatus() {
        override fun toString(): String {
            return "Immutable"
        }
    }

    data class ShallowImmutable(val reasons: List<ShallowImmutableReason> = listOf()) : ImmutabilityStatus() {
        override fun toString(): String {
            return "ShallowImmutable"
        }
    }

    data class ConditionallyDeeplyImmutable(
        val conditions: Set<Int>,
        val reasons: List<ConditionalDeeplyImmutableReason> = listOf()
    ) : ImmutabilityStatus() {
        override fun toString(): String {
            return "ConditionallyDeeplyImmutable($conditions)"
        }
    }

    data class Mutable(val reasons: List<MutableReason> = listOf()) : ImmutabilityStatus() {
        override fun toString(): String {
            return "Mutable"
        }
    }
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
