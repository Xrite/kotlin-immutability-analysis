package test.test.reasons.mutable

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo
import test.test.reasons.location
import test.test.reasons.textWithLocation

class OpenProperty(val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "open property"
        override val info: Map<String, Any?>
            get() = mapOf(*propertyDescriptor.basicInfo)
    }
}
