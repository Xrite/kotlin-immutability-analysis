package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class VarProperty(val variableDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "var"
        override val info: Map<String, Any?>
            get() = mapOf(*variableDescriptor.basicInfo)
    }
}
