package org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.ConditionallyDeeplyImmutableReason
import org.jetbrains.research.ml.kotlinAnalysis.immutability.reasons.basicInfo

data class ValPropertyConditionallyDeeplyImmutable(val byAssumption: Boolean, val propertyDescriptor: PropertyDescriptor) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) "(assumption)" else ""
                return "val refers to conditionally deeply immutable type $assumption".collapseSpaces()
            }
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }
}
