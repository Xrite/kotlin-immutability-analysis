package test.test

sealed class ImmutabilityStatus {
    object Immutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "Immutable"
        }
    }

    object ShallowImmutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "ShallowImmutable"
        }
    }

    data class ConditionallyDeeplyImmutable(val conditions: Set<Int>) : ImmutabilityStatus()
    object Mutable : ImmutabilityStatus() {
        override fun toString(): String {
            return "Mutable"
        }
    }
}

fun join(statuses: List<ImmutabilityStatus>): ImmutabilityStatus = when {
    ImmutabilityStatus.Mutable in statuses -> ImmutabilityStatus.Mutable
    ImmutabilityStatus.ShallowImmutable in statuses -> ImmutabilityStatus.ShallowImmutable
    statuses.any { it is ImmutabilityStatus.ConditionallyDeeplyImmutable } -> {
        val conditions = statuses.mapNotNull {
            when (it) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> it.conditions
                else -> null
            }
        }.flatten().toSet()
        ImmutabilityStatus.ConditionallyDeeplyImmutable(conditions)
    }
    else -> ImmutabilityStatus.Immutable
}

fun join(vararg statuses: ImmutabilityStatus): ImmutabilityStatus = join(statuses.toList())
