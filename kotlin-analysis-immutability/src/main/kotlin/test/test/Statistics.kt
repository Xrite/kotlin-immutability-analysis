package test.test

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

class Statistics(private val immutability: Immutability) {
    // TODO: Add error percentage to the results
    fun percentage(): String {
        val results = immutability.results()
        val immutable = results.filter { it.second is ImmutabilityStatus.Immutable }
        val shallowImmutable = results.filter { it.second is ImmutabilityStatus.ShallowImmutable }
        val mutable = results.filter { it.second is ImmutabilityStatus.Mutable }
        val conditionalDeeplyImmutable = results.filter { it.second is ImmutabilityStatus.ConditionallyDeeplyImmutable }
        val total = results.size
        val unresolved = immutability.unresolvedEntities()
        return """
            Immutable: ${immutable.size} (${immutable.size / total.toDouble() * 100}%)
            ShallowImmutable: ${shallowImmutable.size} (${shallowImmutable.size / total.toDouble() * 100}%)
            ConditionalDeeplyImmutable: ${conditionalDeeplyImmutable.size} (${conditionalDeeplyImmutable.size / total.toDouble() * 100}%)
            Mutable: ${mutable.size} (${mutable.size / total.toDouble() * 100}%)
            Total: $total
            Unresolved: ${unresolved.size}
        """.trimIndent()
    }
}
