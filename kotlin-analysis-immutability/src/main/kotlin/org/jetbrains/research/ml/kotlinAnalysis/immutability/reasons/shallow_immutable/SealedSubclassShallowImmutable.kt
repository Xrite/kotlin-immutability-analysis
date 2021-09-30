package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.shallow_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ShallowImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class SealedSubclassShallowImmutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) :
    ShallowImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.SHALLOW_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass shallow immutable (assumption)"
                Type.SHALLOW_IMMUTABLE -> "sealed subclass shallow immutable"
            }
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }

    enum class Type {
        SHALLOW_IMMUTABLE_BY_ASSUMPTION,
        SHALLOW_IMMUTABLE,
    }
}
