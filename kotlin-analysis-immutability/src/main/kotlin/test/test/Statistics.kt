package test.test

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
        return """
            Immutable: ${immutable.size} (${immutable.size / total.toDouble() * 100}%)
            ShallowImmutable: ${shallowImmutable.size} (${shallowImmutable.size / total.toDouble() * 100}%)
            ConditionalDeeplyImmutable: ${conditionalDeeplyImmutable.size} (${conditionalDeeplyImmutable.size / total.toDouble() * 100}%)
            Mutable: ${mutable.size} (${mutable.size / total.toDouble() * 100}%)
            Total: $total
        """.trimIndent()
    }

    // TODO: consider using better libs for CSV writing
    fun writeCSV(projectName: String): String {
        val results = immutability.results()
        return results.joinToString(separator = System.lineSeparator()) {
            val entity = it.first
            val result = when(it.second) {
                is ImmutabilityStatus.ConditionallyDeeplyImmutable -> "ConditionallyDeeplyImmutable"
                is ImmutabilityStatus.Immutable -> "Immutable"
                is ImmutabilityStatus.Mutable -> "Mutable"
                is ImmutabilityStatus.ShallowImmutable -> "ShallowImmutable"
            }
            val name = when (entity) {
                is ClassTemplate -> entity.desc.fqNameSafe.asString()
                ErrorTemplate -> "ERROR"
            }
            val type = when (entity) {
                is ClassTemplate -> entity.classType.name
                ErrorTemplate -> "ERROR"
            }
            "$projectName, $name, $type, $result"
        }
    }
}
