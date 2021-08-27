package test.test.reasons.conditionally_deeply_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.basicInfo

class OuterClassTypeConditionallyDeeplyImmutable(val byAssumption: Boolean, val classifierDescriptor: ClassifierDescriptor) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) " (assumption)" else ""
                return "Outer class conditionally deeply immutable $assumption".collapseSpaces()
            }
        override val info: String
            get() = json {
                obj(*classifierDescriptor.basicInfo)
            }.toJsonString(true)
    }
}
