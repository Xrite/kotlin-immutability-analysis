package test.test

import org.jetbrains.kotlin.load.kotlin.toSourceElement

private fun ClassTemplate.calcStatus(
    immutabilityMap: ImmutabilityMap
): ImmutabilityProperty {
    val resolve = immutabilityMap.Resolver(this.parameters)
    val neighbors = this.dependencies.map { dependency ->
        dependency.recalculate {
            try {
                resolve(it)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Failed to resolve type $it in ${this.desc.toSourceElement.containingFile}", e)
            }
        }
    }
    return join(neighbors)
}

fun solve(entities: List<Entity>, vararg assumptions: Assumptions): ImmutabilityMap {
    val immutability = ImmutabilityMap(entities, *assumptions)
    var iter = 0
    while (true) {
        iter++
        var updated = false
        entities.shuffled().forEach { entity ->
            val newStatus = when (entity) {
                is ClassTemplate -> entity.calcStatus(immutability)
                ErrorTemplate -> ImmutabilityProperty.Mutable()
            }
            if (immutability.set(entity, newStatus)) {
                updated = true
            }
        }
        if (!updated) {
            break
        }
    }
    println("Converged in $iter iterations")
    return immutability
}
