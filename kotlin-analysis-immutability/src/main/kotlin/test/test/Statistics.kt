package test.test

import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

class Statistics(private val immutability: Immutability) {
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

    fun writeCSV(): String {
        val results = immutability.results()
        return results.joinToString(separator = "\n") {
            val entity = it.first
            val result = it.second
            val name = when (entity) {
                is ClassTemplate -> entity.desc.fqNameSafe.asString()
                is ObjectTemplate -> entity.desc.fqNameSafe.asString()
                ErrorTemplate -> "ERROR"
            }
            val type = when (entity) {
                is ClassTemplate -> entity.classType.name
                is ObjectTemplate -> entity.objectType.name
                ErrorTemplate -> "ERROR"
            }
            name + ", " + type + ", " + result
        }
    }
}
