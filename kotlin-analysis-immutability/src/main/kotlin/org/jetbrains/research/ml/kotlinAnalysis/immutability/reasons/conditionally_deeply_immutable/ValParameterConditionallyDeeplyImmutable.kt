package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ConditionallyDeeplyImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class ValParameterConditionallyDeeplyImmutable(
    val byAssumption: Boolean,
    val parameterDescriptor: ValueParameterDescriptor
) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) "(assumption)" else ""
                return "val (parameter) refers to conditionally deeply immutable type $assumption".collapseSpaces()
            }
        override val info: Map<String, Any?>
            get() = mapOf(*parameterDescriptor.basicInfo)
    }
}
