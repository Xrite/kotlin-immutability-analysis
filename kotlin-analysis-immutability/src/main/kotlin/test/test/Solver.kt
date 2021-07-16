package test.test

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor

fun ClassTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable
            is Dependency.Parent -> immutability[it.type]
            is Dependency.ValTo -> {
                when(val status = immutability[it.type]) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    ImmutabilityStatus.Immutable -> status
                    ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable
                    ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
                }
            }
            is Dependency.VarTo -> ImmutabilityStatus.Mutable
            is Dependency.Error -> ImmutabilityStatus.Mutable
            is Dependency.Outer -> TODO()
        }
    }
    return join(neighbors)
}

fun ObjectTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable
            is Dependency.Parent -> immutability[it.type]
            is Dependency.ValTo -> {
                when(val status = immutability[it.type]) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    ImmutabilityStatus.Immutable -> status
                    ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable
                    ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
                }
            }
            is Dependency.VarTo -> ImmutabilityStatus.Mutable
            is Dependency.Error -> ImmutabilityStatus.Mutable
            is Dependency.Outer -> TODO()
        }
    }
    return join(neighbors)
}

/*
fun ParameterizedClassTemplate.calcStatus(
    statuses: Map<Entity, ImmutabilityStatus>,
    index: Map<DeclarationDescriptor, Entity>,
    immutability: Immutability
): ImmutabilityStatus {
    val neighbors = this.dependencies.map {
        when (it) {
            is Dependency.DebugType -> ImmutabilityStatus.Immutable
            is Dependency.Parent -> immutability[it.type]
            is Dependency.ValTo -> {
                when(val status = immutability[it.type]) {
                    is ImmutabilityStatus.ConditionallyDeeplyImmutable -> status
                    ImmutabilityStatus.Immutable -> status
                    ImmutabilityStatus.Mutable -> ImmutabilityStatus.ShallowImmutable
                    ImmutabilityStatus.ShallowImmutable -> ImmutabilityStatus.ShallowImmutable
                }
            }
            is Dependency.VarTo -> ImmutabilityStatus.Mutable
            is Dependency.Error -> ImmutabilityStatus.Mutable
            is Dependency.Outer -> TODO()
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
        entities.forEach { entity ->
            val newStatus = when (entity) {
                is ClassTemplate -> entity.calcStatus(immutability)
                is ObjectTemplate -> entity.calcStatus(immutability)
                //is ParameterizedClassTemplate -> entity.calcStatus(mp, index)
                ErrorTemplate -> ImmutabilityStatus.Mutable
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
