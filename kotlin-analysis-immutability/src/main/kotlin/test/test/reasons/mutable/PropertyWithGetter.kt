package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class PropertyWithGetter(val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Property with getter"
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }
}
