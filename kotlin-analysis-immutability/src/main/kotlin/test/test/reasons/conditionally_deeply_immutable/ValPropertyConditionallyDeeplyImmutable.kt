package test.test.reasons.conditionally_deeply_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.basicInfo

class ValPropertyConditionallyDeeplyImmutable(val byAssumption: Boolean, val propertyDescriptor: PropertyDescriptor) :
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
