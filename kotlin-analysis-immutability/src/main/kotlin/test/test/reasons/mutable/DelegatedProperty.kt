package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class DelegatedProperty(val descriptor: VariableDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Delegated property"
        override val info: Map<String, Any?>
            get() = mapOf(*descriptor.basicInfo)
    }
}
