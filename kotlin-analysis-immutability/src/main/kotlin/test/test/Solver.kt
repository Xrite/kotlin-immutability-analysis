package test.test

fun ClassTemplate.resolveParameters(result: Immutability.Result): ImmutabilityStatus = when (result) {
    is Immutability.Result.ConditionallyDeeplyImmutable -> {
        val indices = this.parameters.associate { it.original.typeConstructor to it.index }
        val conditions = result.conditions.mapNotNull {
            indices[it.type.constructor]
        }.toSet()
        ImmutabilityStatus.ConditionallyDeeplyImmutable(conditions)
    }
    Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
    Immutability.Result.Mutable -> ImmutabilityStatus.Mutable()
    Immutability.Result.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
}

/*
fun ObjectTemplate.resolveParameters(result: Immutability.Result): ImmutabilityStatus = when (result) {
    is Immutability.Result.ConditionallyDeeplyImmutable -> {
        throw Exception("Object $this depends on type arguments ${result.conditions}")
    }
    Immutability.Result.Immutable -> ImmutabilityStatus.Immutable()
    Immutability.Result.Mutable -> ImmutabilityStatus.Mutable()
    Immutability.Result.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
}
 */

fun ClassTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable()
            is Dependency.Parent -> this.resolveParameters(immutability[it.type])
            is Dependency.ValTo -> {
                when (val status = this.resolveParameters(immutability[it.type])) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    is ImmutabilityStatus.Immutable -> status
                    is ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable()
                    is ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
                }
            }
            is Dependency.VarTo -> ImmutabilityStatus.Mutable()
            is Dependency.Error -> ImmutabilityStatus.Mutable()
            is Dependency.Outer -> this.resolveParameters(immutability[it.outer.defaultType])
            is Dependency.ValParameter -> {
                when (val status = this.resolveParameters(immutability[it.type])) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    is ImmutabilityStatus.Immutable -> status
                    is ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable()
                    is ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
                }
            }
            is Dependency.VarParameter -> ImmutabilityStatus.Mutable()
        }
    }
    return join(neighbors)
}

/*
fun ObjectTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable()
            is Dependency.Parent -> this.resolveParameters(immutability[it.type])
            is Dependency.ValTo -> {
                when (val status = this.resolveParameters(immutability[it.type])) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    is ImmutabilityStatus.Immutable -> status
                    is ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable()
                    is ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
                }
            }
            is Dependency.VarTo -> ImmutabilityStatus.Mutable()
            is Dependency.Error -> ImmutabilityStatus.Mutable()
            is Dependency.Outer -> this.resolveParameters(immutability[it.outer.defaultType])
            is Dependency.ValParameter -> {
                when (val status = this.resolveParameters(immutability[it.type])) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    is ImmutabilityStatus.Immutable -> status
                    is ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable()
                    is ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable()
                }
            }
            is Dependency.VarParameter -> ImmutabilityStatus.Mutable()
        }
    }
    return join(neighbors)
}
 */

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
