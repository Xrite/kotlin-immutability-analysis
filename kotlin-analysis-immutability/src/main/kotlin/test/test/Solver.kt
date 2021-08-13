package test.test

private fun ClassTemplate.calcStatus(
    immutabilityMap: ImmutabilityMap
): ImmutabilityProperty {
    val resolve = immutabilityMap.Resolver(this.parameters)
    val neighbors = this.dependencies.map { dependency ->
        dependency.recalculate { resolve(it) }
    }
    return join(neighbors)
}

fun solve(entities: List<Entity>, vararg assumptions: Assumptions): ImmutabilityMap {
    val immutability = ImmutabilityMap(entities, *assumptions)
    var iter = 0
    while (true) {
        println(iter)
        var updated = false
        entities.shuffled().forEach { entity ->
            val newStatus = when (entity) {
                is ClassTemplate -> entity.calcStatus(immutability)
                ErrorTemplate -> ImmutabilityProperty.Mutable()
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
