package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.MutableReason
import test.test.reasons.basicInfo

class PropertyWithGetter(val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason = "Property with getter"
        override val info: String
            get() = json {
                obj(*propertyDescriptor.basicInfo)
            }.toJsonString(true)
    }
}
