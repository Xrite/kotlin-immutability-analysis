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

    data class ConditionallyDeeplyImmutable(val conditions: Set<Int>, val reasons: List<ConditionalDeeplyImmutableReason> = listOf()) :
        ImmutabilityStatus()

    data class Mutable(val reasons: List<MutableReason> = listOf()) : ImmutabilityStatus() {
        override fun toString(): String {
            return "Mutable"
        }
    }
}

fun join(statuses: List<ImmutabilityStatus>): ImmutabilityStatus {
    return when {
        statuses.anyInstance<ImmutabilityStatus.Mutable>() -> {
            val reasons = statuses.filterIsInstance<ImmutabilityStatus.Mutable>().flatMap {
                it.reasons
            }
            ImmutabilityStatus.Mutable(reasons)
        }
        statuses.anyInstance<ImmutabilityStatus.ShallowImmutable>() -> {
            val reasons = statuses.filterIsInstance<ImmutabilityStatus.ShallowImmutable>().flatMap {
                it.reasons
            }
            ImmutabilityStatus.ShallowImmutable(reasons)
        }
        statuses.any { it is ImmutabilityStatus.ConditionallyDeeplyImmutable } -> {
            val conditions = statuses.mapNotNull {
                when (it) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> it.conditions
                    else -> null
                }
            }.flatten().toSet()
            val reasons = statuses.filterIsInstance<ImmutabilityStatus.ConditionallyDeeplyImmutable>().flatMap {
                it.reasons
            }
            ImmutabilityStatus.ConditionallyDeeplyImmutable(conditions, reasons)
        }
        else -> {
            val reasons = statuses.filterIsInstance<ImmutabilityStatus.Immutable>().flatMap {
                it.reasons
            }
            ImmutabilityStatus.Immutable(reasons)
        }
    }
}

fun join(vararg statuses: ImmutabilityStatus): ImmutabilityStatus = join(statuses.toList())
