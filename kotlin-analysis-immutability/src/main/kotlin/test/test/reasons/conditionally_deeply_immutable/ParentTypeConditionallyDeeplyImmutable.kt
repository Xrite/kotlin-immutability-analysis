package test.test.reasons.conditionally_deeply_immutable

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.basicInfo

class ParentTypeConditionallyDeeplyImmutable(val byAssumption: Boolean, val classifierDescriptor: ClassifierDescriptor) : ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) " (assumption)" else ""
                return "Parent type conditionally deeply immutable $assumption".collapseSpaces()
            }
        override val info: Map<String, Any?>
            get() = mapOf(*classifierDescriptor.basicInfo)
    }
}
