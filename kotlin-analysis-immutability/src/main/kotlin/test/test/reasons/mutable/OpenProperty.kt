package test.test.reasons.mutable

import com.beust.klaxon.json
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import test.test.reasons.MutableReason

class OpenProperty(val propertyDescriptor: PropertyDescriptor) : MutableReason() {
    override val csvData = object : CSVData {
        override val reason: String
            get() = "open property"
        override val info: String
            get() = json {
                obj(
                    "descriptor" to propertyDescriptor.toString(),
                )
            }.toJsonString(true)
    }
}
