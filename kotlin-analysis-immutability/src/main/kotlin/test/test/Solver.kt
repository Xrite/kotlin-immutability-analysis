package test.test

import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.types.KotlinType

private fun ClassTemplate.calcStatus(
    immutabilityMap: MutableImmutabilityMap
): ImmutabilityProperty {
    val resolve = immutabilityMap.WithContext(this.parameters)
    val neighbors = this.dependencies.map { dependency ->
        dependency.recalculate(object : ImmutabilityWithContext by resolve {
            override fun invoke(type: KotlinType): ImmutabilityWithContext.Result = try {
                resolve(type)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Failed to resolve type $type in ${this@calcStatus.desc.toSourceElement.containingFile}", e)
            }
        })
    }
    return join(neighbors)
}

fun solve(entities: List<Entity>, vararg assumptions: Assumptions): Immutability {
    val immutability = MutableImmutabilityMap(entities, *assumptions)
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
