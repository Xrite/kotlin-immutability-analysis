package test.test

private fun ClassTemplate.calcStatus(
    immutability: Immutability
): ImmutabilityStatus {
    val resolve = immutability.WithContext(this.parameters)
    val neighbors = this.dependencies.map { dependency ->
        dependency.recalculate { resolve(it) }
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
