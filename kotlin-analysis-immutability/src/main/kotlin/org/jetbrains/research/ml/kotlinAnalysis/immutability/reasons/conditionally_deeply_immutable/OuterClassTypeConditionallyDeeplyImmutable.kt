package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ConditionallyDeeplyImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class OuterClassTypeConditionallyDeeplyImmutable(
    val byAssumption: Boolean,
    val classifierDescriptor: ClassifierDescriptor
) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) " (assumption)" else ""
                return "Outer class conditionally deeply immutable $assumption".collapseSpaces()
            }
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }
}
