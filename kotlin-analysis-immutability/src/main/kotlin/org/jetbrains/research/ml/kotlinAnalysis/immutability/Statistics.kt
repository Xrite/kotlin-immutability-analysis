package org.jetbrains.research.ml.kotlinAnalysis.immutability

/** Class for printing brief summary statistic */
class Statistics(private val immutabilityMap: Immutability) {
    fun percentage(): String {
        val results = immutabilityMap.results()
        val immutable = results.filter { it.second is ImmutabilityProperty.Immutable }
        val shallowImmutable = results.filter { it.second is ImmutabilityProperty.ShallowImmutable }
        val mutable = results.filter { it.second is ImmutabilityProperty.Mutable }
        val conditionalDeeplyImmutable =
            results.filter { it.second is ImmutabilityProperty.ConditionallyDeeplyImmutable }
        val total = results.size
        val unresolved = immutabilityMap.unresolvedEntities()
        return """
            Immutable: ${immutable.size} (${immutable.size / total.toDouble() * 100}%)
            ShallowImmutable: ${shallowImmutable.size} (${shallowImmutable.size / total.toDouble() * 100}%)
            ConditionalDeeplyImmutable: ${conditionalDeeplyImmutable.size} (${conditionalDeeplyImmutable.size / total.toDouble() * 100}%)
            Mutable: ${mutable.size} (${mutable.size / total.toDouble() * 100}%)
            Total: $total
            Unresolved: ${unresolved.size} (${unresolved.size / total.toDouble() * 100}%)
        """.trimIndent()
    }
}
