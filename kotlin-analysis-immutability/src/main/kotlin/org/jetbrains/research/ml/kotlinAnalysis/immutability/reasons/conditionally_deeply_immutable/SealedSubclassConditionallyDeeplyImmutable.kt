package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ConditionallyDeeplyImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class SealedSubclassConditionallyDeeplyImmutable(val type: Type, val classifierDescriptor: ClassifierDescriptor) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = when (type) {
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION -> "sealed subclass conditionally deeply immutable (assumption)"
                Type.CONDITIONALLY_DEEPLY_IMMUTABLE -> "sealed subclass conditionally deeply immutable"
            }
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }

    enum class Type {
        CONDITIONALLY_DEEPLY_IMMUTABLE_BY_ASSUMPTION,
        CONDITIONALLY_DEEPLY_IMMUTABLE,
    }
}
