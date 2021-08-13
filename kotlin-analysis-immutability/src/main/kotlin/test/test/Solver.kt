package test.test

import test.test.Immutability.Result.Reason.*

private fun ClassTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        val resolve = immutability.WithContext(this.parameters)
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable()
            is Dependency.Parent -> when (val status = resolve(it.type)) {
                is Immutability.Result.ConditionallyDeeplyImmutable -> {
                    val reason = when(status.reason) {
                        ASSUMPTION -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(true)
                        UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                        RESOLVED -> ConditionallyDeeplyImmutableReason.ParentTypeConditionallyDeeplyImmutable(false)
                    }
                    ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                }
                is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                is Immutability.Result.Mutable -> {
                    val reason = when (status.reason) {
                        ASSUMPTION -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE_BY_ASSUMPTION)
                        UNKNOWN -> MutableReason.ParentType(MutableReason.ParentType.Type.UNKNOWN)
                        RESOLVED -> MutableReason.ParentType(MutableReason.ParentType.Type.MUTABLE)
                    }
                    ImmutabilityStatus.Mutable(reason)
                }
                is Immutability.Result.ShallowImmutable -> {
                    val reason = when (status.reason) {
                        ASSUMPTION -> ShallowImmutableReason.ParentTypeShallowImmutable(true)
                        UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                        RESOLVED -> ShallowImmutableReason.ParentTypeShallowImmutable(false)
                    }
                    ImmutabilityStatus.ShallowImmutable(reason)
                }
            }
            is Dependency.ValProperty -> {
                when (val status = resolve(it.type)) {
                    is Immutability.Result.ConditionallyDeeplyImmutable -> {
                        val reason = when(status.reason) {
                            ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                                byAssumption = true,
                                isParameter = false
                            )
                            UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                            RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                                byAssumption = false,
                                isParameter = false
                            )
                        }
                        ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                    }
                    is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                    is Immutability.Result.Mutable -> {
                        val reason = when (status.reason) {
                            ASSUMPTION -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION, false)
                            UNKNOWN -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.UNKNOWN, false)
                            RESOLVED -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.MUTABLE, false)
                        }
                        ImmutabilityStatus.ShallowImmutable(reason)
                    }
                    is Immutability.Result.ShallowImmutable -> {
                        val reason = when(status.reason) {
                            ASSUMPTION -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION, false)
                            UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                            RESOLVED -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.SHALLOW_IMMUTABLE, false)
                        }
                        ImmutabilityStatus.ShallowImmutable(reason)
                    }
                }
            }
            is Dependency.VarProperty -> ImmutabilityStatus.Mutable(MutableReason.VarProperty(false))
            is Dependency.Error -> ImmutabilityStatus.Mutable(MutableReason.Error)
            is Dependency.Outer -> {
                when (val status = resolve(it.outer.defaultType)) {
                    is Immutability.Result.ConditionallyDeeplyImmutable -> {
                        val reason = when (status.reason) {
                            ASSUMPTION -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(true)
                            UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                            RESOLVED -> ConditionallyDeeplyImmutableReason.OuterClassTypeConditionallyDeeplyImmutable(false)
                        }
                        ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                    }
                    is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                    is Immutability.Result.Mutable -> {
                        val reason = when (status.reason) {
                            ASSUMPTION -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE_BY_ASSUMPTION)
                            UNKNOWN -> throw IllegalArgumentException("Outer class unknown")
                            RESOLVED -> MutableReason.OuterClass(MutableReason.OuterClass.Type.MUTABLE)
                        }
                        ImmutabilityStatus.Mutable(reason)
                    }
                    is Immutability.Result.ShallowImmutable -> {
                        val reason = when (status.reason) {
                            ASSUMPTION -> ShallowImmutableReason.OuterClassShallowImmutable(true)
                            UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                            RESOLVED -> ShallowImmutableReason.OuterClassShallowImmutable(false)
                        }
                        ImmutabilityStatus.ShallowImmutable(reason)
                    }
                }
            }
            is Dependency.ValParameter -> {
                when (val status = resolve(it.type)) {
                    is Immutability.Result.ConditionallyDeeplyImmutable -> {
                        val reason = when(status.reason) {
                            ASSUMPTION -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                                byAssumption = true,
                                isParameter = true
                            )
                            UNKNOWN -> throw IllegalArgumentException("ConditionallyDeeplyImmutable unknown")
                            RESOLVED -> ConditionallyDeeplyImmutableReason.ValPropertyConditionallyDeeplyImmutable(
                                byAssumption = false,
                                isParameter = true
                            )
                        }
                        ImmutabilityStatus.ConditionallyDeeplyImmutable(status.conditions, reason)
                    }
                    is Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
                    is Immutability.Result.Mutable -> {
                        val reason = when(status.reason) {
                            ASSUMPTION -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.MUTABLE_BY_ASSUMPTION, true)
                            UNKNOWN -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.UNKNOWN, true)
                            RESOLVED -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.MUTABLE, true)
                        }
                        ImmutabilityStatus.ShallowImmutable(reason)
                    }
                    is Immutability.Result.ShallowImmutable -> {
                        val reason = when(status.reason) {
                            ASSUMPTION -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION, true)
                            UNKNOWN -> throw IllegalArgumentException("ShallowImmutable unknown")
                            RESOLVED -> ShallowImmutableReason.ValProperty(ShallowImmutableReason.Type.SHALLOW_IMMUTABLE, true)
                        }
                        ImmutabilityStatus.ShallowImmutable(reason)
                    }
                }
            }
            is Dependency.VarParameter -> ImmutabilityStatus.Mutable(MutableReason.VarProperty(true))
            is Dependency.DelegatedValProperty -> ImmutabilityStatus.Mutable(MutableReason.DelegatedProperty)
            is Dependency.PropertyWithGetter -> ImmutabilityStatus.Mutable(MutableReason.PropertyWithGetter)
        }
    }
    return join(neighbors)
}

fun solve(entities: List<Entity>, vararg assumptions: Assumptions): Immutability {
    val immutability = Immutability(entities, *assumptions)
    var iter = 0
    while (true) {
        println(iter)
        var updated = false
        entities.shuffled().forEach { entity ->
            val newStatus = when (entity) {
                is ClassTemplate -> entity.calcStatus(immutability)
                ErrorTemplate -> ImmutabilityStatus.Mutable()
            }
            if (immutability.update(entity, newStatus)) {
                updated = true
            }
        }
        if (!updated) {
            break
        }
        iter++
    }
    return immutability
}
