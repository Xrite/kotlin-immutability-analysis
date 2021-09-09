package test.test.reasons.conditionally_deeply_immutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.idea.util.string.collapseSpaces
import test.test.reasons.ConditionallyDeeplyImmutableReason
import test.test.reasons.basicInfo

class ValParameterConditionallyDeeplyImmutable(val byAssumption: Boolean, val parameterDescriptor: ValueParameterDescriptor) :
    ConditionallyDeeplyImmutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() {
                val assumption = if (byAssumption) "(assumption)" else ""
                return "val (parameter) refers to conditionally deeply immutable type $assumption".collapseSpaces()
            }
        override val info: String
            get() = json {
                obj(*parameterDescriptor.basicInfo)
            }.toJsonString(true)
    }
}
